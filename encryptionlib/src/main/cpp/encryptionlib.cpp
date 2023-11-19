#include <jni.h>
#include <string>
#include <cstdio>
#include <cstdlib>
#include <openssl/evp.h>
#include <openssl/aes.h>
#include <android/log.h>
#include <fstream>
#include "vector"
#include "openssl/err.h"
#include "crypto/evp.h"
#include <android/file_descriptor_jni.h>
#include "unistd.h"
#include "filesystem"
#include "iostream"

#define APPNAME "FileEncryptor"

const int RESULT_ERROR = 1;
const int RESULT_SUCCESS = 0;

const char *const ALGO_AES = "AES";
const char *const ALGO_TRIPLE_DES = "TripleDES";
const char *const ALGO_BLOWFISH = "Blowfish";
const char *const ALGO_CAMELLIA = "Camellia";

const char *const MODE_CBC = "CBC";
const char *const MODE_ECB = "ECB";
const char *const MODE_OFB = "OFB";
const char *const MODE_CFB = "CFB";

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
    unsigned char key[] = {0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37,
                           0x38, 0x39, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35,
                           0x36, 0x37, 0x38, 0x39, 0x30, 0x31, 0x32, 0x33,
                           0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30, 0x31
    };

    /* A 128 bit IV */
    unsigned char iv[] = {0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37,
                          0x38, 0x39, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35
    };

    /* Create and initialise the context */
    if (!(evpEncCtx = EVP_CIPHER_CTX_new())) {
        __android_log_print(ANDROID_LOG_ERROR, APPNAME,
                            "Cannot init OpenSSL, Encryption context is null");
        return;
    }
    if (!(evpDecCtx = EVP_CIPHER_CTX_new())) {
        __android_log_print(ANDROID_LOG_ERROR, APPNAME,
                            "Cannot init OpenSSL, Decryption context is null");
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

int open_input_file(std::ifstream &file, const std::string &file_path) {
    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Opening file to encrypt...");
    file = std::ifstream(file_path);
    if (!file.is_open()) {
        __android_log_print(ANDROID_LOG_ERROR, APPNAME, "Could not open file with path: %s",
                            file_path.c_str());
        return RESULT_ERROR;
    } else {
        __android_log_print(ANDROID_LOG_DEBUG, APPNAME, "File is opened");
        return RESULT_SUCCESS;
    }
}

int open_output_file(std::ofstream &file, const std::string &file_path) {
    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Opening file to encrypt...");
    file = std::ofstream(file_path);
    if (!file.is_open()) {
        __android_log_print(ANDROID_LOG_ERROR, APPNAME, "Could not open file");
        return RESULT_ERROR;
    } else {
        __android_log_print(ANDROID_LOG_DEBUG, APPNAME, "File is opened");
        return RESULT_SUCCESS;
    }
}

struct CipherInfo {
    const EVP_CIPHER *evp_cipher;
    const EVP_MD *evp_md;
    int16_t key_len_bytes;
    int16_t cipher_block_size;
};

int get_cipher_info(
        const unsigned char *algorithm,
        const unsigned char *mode,
        CipherInfo *info
) {
    info->cipher_block_size = 128;
    if (std::strcmp(reinterpret_cast<const char *>(algorithm), ALGO_AES) == 0) {
        info->key_len_bytes = 32;
        info->evp_md = EVP_sha1();
        if (std::strcmp(reinterpret_cast<const char *>(mode), MODE_CBC) == 0) {
            info->evp_cipher = EVP_aes_256_cbc();
        } else if (std::strcmp(reinterpret_cast<const char *>(mode), MODE_CFB) == 0) {
            info->evp_cipher = EVP_aes_256_cfb128();
        } else if (std::strcmp(reinterpret_cast<const char *>(mode), MODE_ECB) == 0) {
            info->evp_cipher = EVP_aes_256_ecb();
        } else if (std::strcmp(reinterpret_cast<const char *>(mode), MODE_OFB) == 0) {
            info->evp_cipher = EVP_aes_256_ofb();
        }
    } else if (std::strcmp(reinterpret_cast<const char *>(algorithm), ALGO_BLOWFISH) == 0) {
        info->key_len_bytes = 32;
        info->evp_md = EVP_sha1();
        if (std::strcmp(reinterpret_cast<const char *>(mode), MODE_CBC) == 0) {
            info->evp_cipher = EVP_bf_cbc();
        } else if (std::strcmp(reinterpret_cast<const char *>(mode), MODE_CFB) == 0) {
            info->evp_cipher = EVP_bf_cfb();
        } else if (std::strcmp(reinterpret_cast<const char *>(mode), MODE_ECB) == 0) {
            info->evp_cipher = EVP_bf_ecb();
        } else if (std::strcmp(reinterpret_cast<const char *>(mode), MODE_OFB) == 0) {
            info->evp_cipher = EVP_bf_ofb();
        }
    } else if (std::strcmp(reinterpret_cast<const char *>(algorithm), ALGO_CAMELLIA) == 0) {
        info->key_len_bytes = 32;
        info->evp_md = EVP_sha1();
        if (std::strcmp(reinterpret_cast<const char *>(mode), MODE_CBC) == 0) {
            info->evp_cipher = EVP_camellia_256_cbc();
        } else if (std::strcmp(reinterpret_cast<const char *>(mode), MODE_CFB) == 0) {
            info->evp_cipher = EVP_camellia_256_cfb();
        } else if (std::strcmp(reinterpret_cast<const char *>(mode), MODE_ECB) == 0) {
            info->evp_cipher = EVP_camellia_256_ecb();
        } else if (std::strcmp(reinterpret_cast<const char *>(mode), MODE_OFB) == 0) {
            info->evp_cipher = EVP_camellia_256_ofb();
        }
    } else if (std::strcmp(reinterpret_cast<const char *>(algorithm), ALGO_TRIPLE_DES) == 0) {
        info->key_len_bytes = 32;
        info->evp_md = EVP_sha1();
        if (std::strcmp(reinterpret_cast<const char *>(mode), MODE_CBC) == 0) {
            info->evp_cipher = EVP_des_ede3_cbc();
        } else if (std::strcmp(reinterpret_cast<const char *>(mode), MODE_CFB) == 0) {
            info->evp_cipher = EVP_des_ede3_cfb();
        } else if (std::strcmp(reinterpret_cast<const char *>(mode), MODE_ECB) == 0) {
            info->evp_cipher = EVP_des_ede3_ecb();
        } else if (std::strcmp(reinterpret_cast<const char *>(mode), MODE_OFB) == 0) {
            info->evp_cipher = EVP_des_ede3_ofb();
        }
    }

    if (info->evp_cipher == nullptr) {
        __android_log_print(ANDROID_LOG_ERROR, APPNAME, "Unknown Algorithm/Mode pair");
        return RESULT_ERROR;
    }

    return RESULT_SUCCESS;
}

EVP_CIPHER_CTX *get_encryption_ctx(
        const unsigned char *algorithm,
        const unsigned char *mode,
        const unsigned char *key_data,
        CipherInfo cipherInfo
) {
    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Initializing OpenSSL encryption...");
    EVP_CIPHER_CTX *encryptionCtx;
    if (!(encryptionCtx = EVP_CIPHER_CTX_new())) {
        __android_log_print(ANDROID_LOG_ERROR, APPNAME,
                            "Cannot init OpenSSL, Encryption context is null");
        return nullptr;
    } else {
        std::vector<unsigned char> key(cipherInfo.key_len_bytes);
        std::vector<unsigned char> iv(cipherInfo.key_len_bytes);
        if (get_cipher_info(algorithm, mode, &cipherInfo) == 0) {
            int generated_key_len = EVP_BytesToKey(cipherInfo.evp_cipher, cipherInfo.evp_md,
                                                   nullptr, key_data,
                                                   cipherInfo.key_len_bytes, 10, key.data(),
                                                   iv.data());

            if (generated_key_len != cipherInfo.key_len_bytes) {
                __android_log_print(ANDROID_LOG_ERROR, APPNAME,
                                    "Cannot init OpenSSL, Generated key is not of the correct length, generated size: %i, correct size: %i",
                                    generated_key_len, cipherInfo.key_len_bytes);
                return nullptr;
            } else {
                EVP_CIPHER_CTX_init(encryptionCtx);
                EVP_EncryptInit_ex(encryptionCtx, cipherInfo.evp_cipher, NULL, key.data(),
                                   iv.data());
                __android_log_print(ANDROID_LOG_DEBUG, APPNAME,
                                    "Encryption context is initialized");
                return encryptionCtx;
            }
        } else {
            return nullptr;
        }
    }
}

EVP_CIPHER_CTX *get_decryption_ctx(
        const unsigned char *algorithm,
        const unsigned char *mode,
        const unsigned char *key_data,
        CipherInfo cipherInfo
) {
    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Initializing OpenSSL encryption...");
    EVP_CIPHER_CTX *decryptionCtx;
    if (!(decryptionCtx = EVP_CIPHER_CTX_new())) {
        __android_log_print(ANDROID_LOG_ERROR, APPNAME,
                            "Cannot init OpenSSL, Decryption context is null");
        return nullptr;
    } else {
        std::vector<unsigned char> key(cipherInfo.key_len_bytes);
        std::vector<unsigned char> iv(cipherInfo.key_len_bytes);
        if (get_cipher_info(algorithm, mode, &cipherInfo) == 0) {
            int generated_key_len = EVP_BytesToKey(cipherInfo.evp_cipher, cipherInfo.evp_md,
                                                   nullptr, key_data,
                                                   cipherInfo.key_len_bytes, 10, key.data(),
                                                   iv.data());

            if (generated_key_len != cipherInfo.key_len_bytes) {
                __android_log_print(ANDROID_LOG_ERROR, APPNAME,
                                    "Cannot init OpenSSL, Generated key is not of the correct length, generated size: %i, correct size: %i",
                                    generated_key_len, cipherInfo.key_len_bytes);
                return nullptr;
            } else {
                EVP_CIPHER_CTX_init(decryptionCtx);
                EVP_DecryptInit_ex(decryptionCtx, cipherInfo.evp_cipher, NULL, key.data(),
                                   iv.data());
                __android_log_print(ANDROID_LOG_DEBUG, APPNAME,
                                    "Encryption context is initialized");
                return decryptionCtx;
            }
        } else {
            return nullptr;
        }
    }
}

const char *load_java_str(
        jstring str,
        JNIEnv *env
) {
    jboolean isStrCopy;
    const char *convertedCharArr = (env)->GetStringUTFChars(
            str,
            &isStrCopy
    );
    if (isStrCopy == JNI_TRUE) {
        return convertedCharArr;
    } else {
        return nullptr;
    }
}

std::string debugArray(const unsigned char *data, size_t len) {
    std::string s(*data, sizeof(len));
    return s;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_fileencryptor_encryptionlib_NativeLib_encrypt(JNIEnv *env, jobject thiz,
                                                               jstring algo,
                                                               jstring mode,
                                                               jstring key,
                                                               jint input_file_descriptor,
                                                               jint output_file_descriptor) {
    const char *algo_converted = load_java_str(algo, env);
    const char *mode_converted = load_java_str(mode, env);
    const char *key_converted = load_java_str(key, env);
//    const char *in_file_path_converted = load_java_str(in_file_path, env);
//    const char *out_file_path_converted = load_java_str(out_file_path, env);

    CipherInfo cipherInfo = CipherInfo();
    get_cipher_info(
            reinterpret_cast<const unsigned char *>(algo_converted),
            reinterpret_cast<const unsigned char *>(mode_converted),
            &cipherInfo
    );

    EVP_CIPHER_CTX *ctx = get_encryption_ctx(
            reinterpret_cast<const unsigned char *>(algo_converted),
            reinterpret_cast<const unsigned char *>(mode_converted),
            reinterpret_cast<const unsigned char *>(key_converted),
            cipherInfo
    );

    if (ctx != nullptr) {
        std::ifstream input_file;
        std::ofstream output_file;

//        if (!open_input_file(input_file, in_file_path_converted) ||
//            !open_output_file(output_file, out_file_path_converted)) {
//            return RESULT_ERROR;
//        }

        int buffer_size = 64;
        input_file.seekg(0, std::ios::beg);

        int bytesRead, bytesWritten;

        unsigned char in_buf[buffer_size], out_buf[buffer_size + cipherInfo.cipher_block_size];

        while ((bytesRead = read(input_file_descriptor, reinterpret_cast<char *>(in_buf),
                                 buffer_size))) {
            __android_log_print(ANDROID_LOG_DEBUG, APPNAME,
                                "Number of read bytes: %i", bytesRead);

            __android_log_print(ANDROID_LOG_DEBUG, APPNAME,
                                "read bytes: %s", in_buf);
            debugArray(in_buf, bytesRead);

            if (!EVP_EncryptUpdate(ctx, out_buf, &bytesWritten, in_buf,
                                   bytesRead)) {
                EVP_CIPHER_CTX_cleanup(ctx);
                return RESULT_ERROR;
            }
            __android_log_print(ANDROID_LOG_DEBUG, APPNAME,
                                "Number of bytes to write: %i", bytesWritten);

            __android_log_print(ANDROID_LOG_DEBUG, APPNAME,
                                "bytes to write: %s", out_buf);
            debugArray(out_buf, bytesWritten);
            write(output_file_descriptor, out_buf, bytesWritten);
            //output_file.write(reinterpret_cast<const char *>(out_buf), bytesWritten);
        }

        EVP_CIPHER_CTX_cleanup(ctx);

        env->ReleaseStringUTFChars(algo, algo_converted);
        env->ReleaseStringUTFChars(mode, mode_converted);
        env->ReleaseStringUTFChars(key, key_converted);
//        env->ReleaseStringUTFChars(in_file_path, in_file_path_converted);
//        env->ReleaseStringUTFChars(out_file_path, out_file_path_converted);

        return RESULT_SUCCESS;
    }

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
    const char *algo_converted = load_java_str(algo, env);
    const char *mode_converted = load_java_str(mode, env);
    const char *key_converted = load_java_str(key, env);

    CipherInfo cipherInfo = CipherInfo();
    get_cipher_info(
            reinterpret_cast<const unsigned char *>(algo_converted),
            reinterpret_cast<const unsigned char *>(mode_converted),
            &cipherInfo
    );

    EVP_CIPHER_CTX *ctx = get_encryption_ctx(
            reinterpret_cast<const unsigned char *>(algo_converted),
            reinterpret_cast<const unsigned char *>(mode_converted),
            reinterpret_cast<const unsigned char *>(key_converted),
            cipherInfo
    );

    if (ctx != nullptr) {
        int buffer_size = 64;
        int bytesRead, bytesWritten;

        unsigned char in_buf[buffer_size], out_buf[buffer_size + cipherInfo.cipher_block_size];

        while ((bytesRead = read(input_file_descriptor, reinterpret_cast<char *>(in_buf),
                                 buffer_size))) {
            __android_log_print(ANDROID_LOG_DEBUG, APPNAME,
                                "Number of read bytes: %i", bytesRead);

            __android_log_print(ANDROID_LOG_DEBUG, APPNAME,
                                "read bytes: %s", in_buf);
            debugArray(in_buf, bytesRead);

            if (!EVP_DecryptUpdate(ctx, out_buf, &bytesWritten, in_buf,
                                   bytesRead)) {
                EVP_CIPHER_CTX_cleanup(ctx);
                return RESULT_ERROR;
            }
            __android_log_print(ANDROID_LOG_DEBUG, APPNAME,
                                "Number of bytes to write: %i", bytesWritten);

            __android_log_print(ANDROID_LOG_DEBUG, APPNAME,
                                "bytes to write: %s", out_buf);
            debugArray(out_buf, bytesWritten);
            write(output_file_descriptor, out_buf, bytesWritten);
        }

        EVP_CIPHER_CTX_cleanup(ctx);

        env->ReleaseStringUTFChars(algo, algo_converted);
        env->ReleaseStringUTFChars(mode, mode_converted);
        env->ReleaseStringUTFChars(key, key_converted);

        return RESULT_SUCCESS;
    }

    return RESULT_ERROR;
}