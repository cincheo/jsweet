package def.dom;

import def.js.ArrayBufferView;
import def.js.Object;

public class SubtleCrypto extends def.js.Object {
    native public java.lang.Object decrypt(java.lang.String algorithm, CryptoKey key, ArrayBufferView data);
    native public java.lang.Object decrypt(Algorithm algorithm, CryptoKey key, ArrayBufferView data);
    native public java.lang.Object deriveBits(java.lang.String algorithm, CryptoKey baseKey, double length);
    native public java.lang.Object deriveBits(Algorithm algorithm, CryptoKey baseKey, double length);
    native public java.lang.Object deriveKey(java.lang.String algorithm, CryptoKey baseKey, java.lang.String derivedKeyType, java.lang.Boolean extractable, java.lang.String[] keyUsages);
    native public java.lang.Object deriveKey(java.lang.String algorithm, CryptoKey baseKey, Algorithm derivedKeyType, java.lang.Boolean extractable, java.lang.String[] keyUsages);
    native public java.lang.Object deriveKey(Algorithm algorithm, CryptoKey baseKey, java.lang.String derivedKeyType, java.lang.Boolean extractable, java.lang.String[] keyUsages);
    native public java.lang.Object deriveKey(Algorithm algorithm, CryptoKey baseKey, Algorithm derivedKeyType, java.lang.Boolean extractable, java.lang.String[] keyUsages);
    native public java.lang.Object digest(java.lang.String algorithm, ArrayBufferView data);
    native public java.lang.Object digest(Algorithm algorithm, ArrayBufferView data);
    native public java.lang.Object encrypt(java.lang.String algorithm, CryptoKey key, ArrayBufferView data);
    native public java.lang.Object encrypt(Algorithm algorithm, CryptoKey key, ArrayBufferView data);
    native public java.lang.Object exportKey(java.lang.String format, CryptoKey key);
    native public java.lang.Object generateKey(java.lang.String algorithm, java.lang.Boolean extractable, java.lang.String[] keyUsages);
    native public java.lang.Object generateKey(Algorithm algorithm, java.lang.Boolean extractable, java.lang.String[] keyUsages);
    native public java.lang.Object importKey(java.lang.String format, ArrayBufferView keyData, java.lang.String algorithm, java.lang.Boolean extractable, java.lang.String[] keyUsages);
    native public java.lang.Object importKey(java.lang.String format, ArrayBufferView keyData, Algorithm algorithm, java.lang.Boolean extractable, java.lang.String[] keyUsages);
    native public java.lang.Object sign(java.lang.String algorithm, CryptoKey key, ArrayBufferView data);
    native public java.lang.Object sign(Algorithm algorithm, CryptoKey key, ArrayBufferView data);
    native public java.lang.Object unwrapKey(java.lang.String format, ArrayBufferView wrappedKey, CryptoKey unwrappingKey, java.lang.String unwrapAlgorithm, java.lang.String unwrappedKeyAlgorithm, java.lang.Boolean extractable, java.lang.String[] keyUsages);
    native public java.lang.Object unwrapKey(java.lang.String format, ArrayBufferView wrappedKey, CryptoKey unwrappingKey, java.lang.String unwrapAlgorithm, Algorithm unwrappedKeyAlgorithm, java.lang.Boolean extractable, java.lang.String[] keyUsages);
    native public java.lang.Object unwrapKey(java.lang.String format, ArrayBufferView wrappedKey, CryptoKey unwrappingKey, Algorithm unwrapAlgorithm, java.lang.String unwrappedKeyAlgorithm, java.lang.Boolean extractable, java.lang.String[] keyUsages);
    native public java.lang.Object unwrapKey(java.lang.String format, ArrayBufferView wrappedKey, CryptoKey unwrappingKey, Algorithm unwrapAlgorithm, Algorithm unwrappedKeyAlgorithm, java.lang.Boolean extractable, java.lang.String[] keyUsages);
    native public java.lang.Object verify(java.lang.String algorithm, CryptoKey key, ArrayBufferView signature, ArrayBufferView data);
    native public java.lang.Object verify(Algorithm algorithm, CryptoKey key, ArrayBufferView signature, ArrayBufferView data);
    native public java.lang.Object wrapKey(java.lang.String format, CryptoKey key, CryptoKey wrappingKey, java.lang.String wrapAlgorithm);
    native public java.lang.Object wrapKey(java.lang.String format, CryptoKey key, CryptoKey wrappingKey, Algorithm wrapAlgorithm);
    public static SubtleCrypto prototype;
    public SubtleCrypto(){}
}

