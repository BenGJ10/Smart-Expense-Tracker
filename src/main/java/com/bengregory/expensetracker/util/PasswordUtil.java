package com.bengregory.expensetracker.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {
    public static String hashPassword(String password) throws DatabaseException {
        try {
            // A core Java class used for one-way cryptographic hashing.
            MessageDigest digest = MessageDigest.getInstance("SHA-256"); // A secure hashing algorithm from the SHA-2 family.

            /* The resulting SHA-256 hash is a byte array (raw data).
            We convert it to a human-readable hex string (base-16), which can be stored in text format in a database.
            Each byte becomes a 2-digit hex representation. */

            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new DatabaseException("Failed to hash password", e);
        }
    }

    // Compares a plain password to a stored hash to authenticate users
    public static boolean verifyPassword(String password, String hashedPassword) throws DatabaseException {
        String computedHash = hashPassword(password); // Converts a plain password to a hashed hexadecimal string
        return computedHash.equals(hashedPassword);
    }
}


