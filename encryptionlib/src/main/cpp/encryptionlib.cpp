#include <jni.h>
#include <string>
#include <cstdio>
#include <cstdlib>
#include <openssl/evp.h>
#include <openssl/aes.h>
#include <android/log.h>

#define APPNAME "FileEncryptor"


/**
 * Create a 256 bit key and IV using the supplied key_data. salt can be added for taste.
 * Fills in the encryption and decryption ctx objects and returns 0 on success
 **/
int aes_init(unsigned char *key_data, int key_data_len, unsigned char *salt, EVP_CIPHER_CTX *e_ctx,
             EVP_CIPHER_CTX *d_ctx) {
    int i, nrounds = 5;
    unsigned char key[32], iv[32];

    /*
     * Gen key & IV for AES 256 CBC mode. A SHA1 digest is used to hash the supplied key material.
     * nrounds is the number of times the we hash the material. More rounds are more secure but
     * slower.
     */
    i = EVP_BytesToKey(EVP_aes_128_cbc(), EVP_sha1(), salt, key_data, key_data_len, nrounds, key,
                       iv);
    if (i != 32) {
        printf("Key size is %d bits - should be 256 bits\n", i);
        return -1;
    }

    EVP_CIPHER_CTX_init(e_ctx);
    EVP_EncryptInit_ex(e_ctx, EVP_aes_256_cbc(), NULL, key, iv);
    EVP_CIPHER_CTX_init(d_ctx);
    EVP_DecryptInit_ex(d_ctx, EVP_aes_256_cbc(), NULL, key, iv);

    return 0;
}

/*
 * Encrypt *len bytes of data
 * All data going in & out is considered binary (unsigned char[])
 *//*
unsigned char *aes_encrypt(EVP_CIPHER_CTX *e, unsigned char *plaintext, int *len) {
    *//* max ciphertext len for a n bytes of plaintext is n + AES_BLOCK_SIZE -1 bytes *//*
    int c_len = *len + AES_BLOCK_SIZE, f_len = 0;
    unsigned char *ciphertext = malloc(c_len);

    *//* allows reusing of 'e' for multiple encryption cycles *//*
    EVP_EncryptInit_ex(e, NULL, NULL, NULL, NULL);

    *//* update ciphertext, c_len is filled with the length of ciphertext generated,
      *len is the size of plaintext in bytes *//*
    EVP_EncryptUpdate(e, ciphertext, &c_len, plaintext, *len);

    *//* update ciphertext with the final remaining bytes *//*
    EVP_EncryptFinal_ex(e, ciphertext + c_len, &f_len);

    *len = c_len + f_len;
    return ciphertext;
}*/

/*
 * Decrypt *len bytes of ciphertext
 */
/*unsigned char *aes_decrypt(EVP_CIPHER_CTX *e, unsigned char *ciphertext, int *len) {
    *//* plaintext will always be equal to or lesser than length of ciphertext*//*
    int p_len = *len, f_len = 0;
    unsigned char *plaintext = malloc(p_len);

    EVP_DecryptInit_ex(e, NULL, NULL, NULL, NULL);
    EVP_DecryptUpdate(e, plaintext, &p_len, ciphertext, *len);
    EVP_DecryptFinal_ex(e, plaintext + p_len, &f_len);

    *len = p_len + f_len;
    return plaintext;
}*/
/*
int main(int argc, char **argv) {
    *//* "opaque" encryption, decryption ctx structures that libcrypto uses to record
       status of enc/dec operations *//*
    EVP_CIPHER_CTX *en = EVP_CIPHER_CTX_new();
    EVP_CIPHER_CTX *de = EVP_CIPHER_CTX_new();

    *//* 8 bytes to salt the key_data during key generation. This is an example of
       compiled in salt. We just read the bit pattern created by these two 4 byte
       integers on the stack as 64 bits of contigous salt material -
       ofcourse this only works if sizeof(int) >= 4 *//*
    unsigned int salt[] = {12345, 54321};
    unsigned char *key_data;
    int key_data_len, i;
    char *input[] = {"a", "abcd", "this is a test", "this is a bigger test",
                     "\nWho are you ?\nI am the 'Doctor'.\n'Doctor' who ?\nPrecisely!",
                     NULL};

    *//* the key_data is read from the argument list *//*
    key_data = (unsigned char *) argv[1];
    key_data_len = strlen(argv[1]);

    *//* gen key and iv. init the cipher ctx object *//*
    if (aes_init(key_data, key_data_len, (unsigned char *) &salt, en, de)) {
        printf("Couldn't initialize AES cipher\n");
        return -1;
    }

    *//* encrypt and decrypt each input string and compare with the original *//*
    for (i = 0; input[i]; i++) {
        char *plaintext;
        unsigned char *ciphertext;
        int olen, len;

        *//* The enc/dec functions deal with binary data and not C strings. strlen() will
           return length of the string without counting the '\0' string marker. We always
           pass in the marker byte to the encrypt/decrypt functions so that after decryption
           we end up with a legal C string *//*
        olen = len = strlen(input[i]) + 1;

        ciphertext = aes_encrypt(en, (unsigned char *) input[i], &len);
        plaintext = (char *) aes_decrypt(de, ciphertext, &len);

        if (strncmp(plaintext, input[i], olen))
            printf("FAIL: enc/dec failed for \"%s\"\n", input[i]);
        else
            printf("OK: enc/dec ok for \"%s\"\n", plaintext);

        free(ciphertext);
        free(plaintext);
    }

    EVP_CIPHER_CTX_free(en);
    EVP_CIPHER_CTX_free(de);

    return 0;
}*/

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_fileencryptor_encryptionlib_NativeLib_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "BSBS from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_fileencryptor_encryptionlib_NativeLib_initOpenSSL(JNIEnv *env, jobject thiz) {
    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "initializing OpenSSL");

    EVP_CIPHER_CTX *evpEncCtx;
    EVP_CIPHER_CTX *evpDecCtx;

    int len;

    int ciphertext_len;

    /* A 256 bit key */
    unsigned char key[] = { 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37,
                           0x38, 0x39, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35,
                           0x36, 0x37, 0x38, 0x39, 0x30, 0x31, 0x32, 0x33,
                           0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30, 0x31
    };

    /* A 128 bit IV */
    unsigned char iv[] = { 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37,
                          0x38, 0x39, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35
    };

    /* Create and initialise the context */
    if(!(evpEncCtx = EVP_CIPHER_CTX_new())) {
        __android_log_print(ANDROID_LOG_ERROR, APPNAME, "Cannot init OpenSSL, Encryption context is null");
        return;
    }
    if(!(evpDecCtx = EVP_CIPHER_CTX_new())) {
        __android_log_print(ANDROID_LOG_ERROR, APPNAME, "Cannot init OpenSSL, Decryption context is null");
        return;
    }

    aes_init(
            key,
            256,
            iv,
            evpEncCtx,
            evpDecCtx
            );

    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "OpenSSL is initialized successfully");
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_fileencryptor_encryptionlib_NativeLib_encrypt(JNIEnv *env, jobject thiz,
                                                               jstring algo,
                                                               jstring mode,
                                                               jstring key,
                                                               jint input_file_descriptor,
                                                               jint output_file_descriptor) {
    return RESULT_ERROR;

}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_fileencryptor_encryptionlib_NativeLib_decrypt(JNIEnv *env,
                                                               jobject thiz,
                                                               jstring algo,
                                                               jstring mode,
                                                               jstring key,
                                                               jint input_file_descriptor,
                                                               jint output_file_descriptor) {
    return RESULT_ERROR;}