package org.imster.cryptography;

/* Manages un-salvageable exceptions from cryptographic operations */
public class CryptoException extends Exception {
    public CryptoException(String message) {
        super(message);
    }
}