package com.streamwide.fileencrypter.encrypter

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.crypto.tink.subtle.Base64
import java.io.*
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class EncryptionFile(val context: Context) {

    private val secretKeyPref = "encryption_secret_key"

    /**
     * EncryptedSharedPreferences used to save the Encryption/decryption secret key
     */
    private val securedPreferences: SharedPreferences by lazy {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            "Secured_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Saves the provided file data to the specified file path.
     *
     * @param fileData The byte array representing the content of the file to be saved.
     * @param path The file path where the file should be saved.
     * @return True if the file was successfully saved, false otherwise.
     */
    @Throws(Exception::class)
    fun saveFile(fileData: ByteArray, path: String): File {
        val file = File(path)
        val bos = BufferedOutputStream(FileOutputStream(file, false))
        bos.write(fileData)
        bos.flush()
        bos.close()
        return file
    }

    /**
     * Encrypts the provided file data using the AES algorithm.
     *
     * @param fileData The byte array representing the content of the file to be encrypted.
     * @return The encrypted byte array.
     * @throws Exception If an error occurs during the encryption process.
     */
    @Throws(Exception::class)
    fun encryptFile(fileData: ByteArray): ByteArray {
        // Get the secret key
        val data = getSecretKey().encoded

        // Create a SecretKeySpec using the secret key
        val sKeySpec = SecretKeySpec(data, 0, data.size, "AES")

        // Initialize the Cipher for encryption
        val cipher = Cipher.getInstance("AES", "BC")
        cipher.init(Cipher.ENCRYPT_MODE, sKeySpec, IvParameterSpec(ByteArray(cipher.blockSize)))
        // Perform AES encryption and return the result
        return cipher.doFinal(fileData)
    }

    /**
     * Decrypts file.
     *
     * @param filesDir The directory containing the encrypted file.
     * @return The decrypted byte array.
     */
    fun decryptEncryptedFile(filesDir: File): ByteArray {
        // Get the absolute path of the file in the specified directory
        val filePath = filesDir.absolutePath
        // Read the file data from the specified file path
        val fileData = readFile(filePath)
        // Get the secret key used for decryption
        val secretKey = getSecretKey()

        // Decrypt the file data using the obtained secret key
        return decrypt(secretKey, fileData)
    }

    /**
     * Generates a secret key for the AES algorithm.
     *
     * @return The generated SecretKey.
     */
    private fun generateSecretKey(): SecretKey? {
        // Create a SecureRandom instance for secure key generation
        val secureRandom = SecureRandom()

        // Create a KeyGenerator instance for AES algorithm
        val keyGenerator = KeyGenerator.getInstance("AES")
        //generate a key with secure random
        keyGenerator?.init(128, secureRandom)

        // Generate and return the SecretKey
        return keyGenerator?.generateKey()
    }


    /**
     * Saves the provided secret key into EncryptedSharedPreferences.
     *
     * @param sharedPref The SharedPreferences instance.
     * @param secretKey The SecretKey to be saved.
     * @return The encoded key as a String.
     */
    private fun saveSecretKey(sharedPref: SharedPreferences, secretKey: SecretKey): String {
        val encodedKey = Base64.encodeToString(secretKey.encoded, Base64.NO_WRAP)
        sharedPref.edit().putString(secretKeyPref, encodedKey).apply()

        return encodedKey
    }

    /**
     * Reads the contents of a file and returns it as a byte array.
     *
     * @param filePath The path to the file to be read.
     * @return The content of the file as a byte array.
     */
    @Throws(Exception::class)
    private fun readFile(filePath: String): ByteArray {
        val file = File(filePath)

        val fileContents = file.readBytes()
        val inputBuffer = BufferedInputStream(
            FileInputStream(file)
        )
        inputBuffer.read(fileContents)
        // Close the input stream
        inputBuffer.close()

        return fileContents
    }

    /**
     * Retrieves or generates a secret key from EncryptedSharedPreferences.
     *
     * @return The retrieved or generated SecretKey.
     */
    private fun getSecretKey(): SecretKey {

        // Try to retrieve the encoded key from SharedPreferences
        val key = securedPreferences.getString(secretKeyPref, null)

        if (key == null) {
            //generate secure random
            val secretKey = generateSecretKey()
            saveSecretKey(securedPreferences, secretKey!!)
            return secretKey
        }

        val decodedKey = Base64.decode(key, Base64.NO_WRAP)
        // If the key is found, decode it and create a SecretKey instance
        return SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")
    }

    /**
     * Decrypts the provided file data using the AES algorithm.
     *
     * @param yourKey The SecretKey used for decryption.
     * @param fileData The byte array representing the content of the encrypted file.
     * @return The decrypted byte array.
     */
    @Throws(Exception::class)
    private fun decrypt(yourKey: SecretKey, fileData: ByteArray): ByteArray {
        val decrypted: ByteArray
        // Initialize the Cipher for decryption
        val cipher = Cipher.getInstance("AES", "BC")
        cipher.init(Cipher.DECRYPT_MODE, yourKey, IvParameterSpec(ByteArray(cipher.blockSize)))
        // Perform AES decryption
        decrypted = cipher.doFinal(fileData)
        return decrypted
    }

}