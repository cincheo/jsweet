package def.dom;
import def.js.ArrayBufferView;
public class SubtleCrypto extends def.js.Object {
    native public Object decrypt(String algorithm, CryptoKey key, ArrayBufferView data);
    native public Object deriveBits(String algorithm, CryptoKey baseKey, double length);
    native public Object deriveKey(String algorithm, CryptoKey baseKey, String derivedKeyType, Boolean extractable, String[] keyUsages);
    native public Object digest(String algorithm, ArrayBufferView data);
    native public Object encrypt(String algorithm, CryptoKey key, ArrayBufferView data);
    native public Object exportKey(String format, CryptoKey key);
    native public Object generateKey(String algorithm, Boolean extractable, String[] keyUsages);
    native public Object importKey(String format, ArrayBufferView keyData, String algorithm, Boolean extractable, String[] keyUsages);
    native public Object sign(String algorithm, CryptoKey key, ArrayBufferView data);
    native public Object unwrapKey(String format, ArrayBufferView wrappedKey, CryptoKey unwrappingKey, String unwrapAlgorithm, String unwrappedKeyAlgorithm, Boolean extractable, String[] keyUsages);
    native public Object verify(String algorithm, CryptoKey key, ArrayBufferView signature, ArrayBufferView data);
    native public Object wrapKey(String format, CryptoKey key, CryptoKey wrappingKey, String wrapAlgorithm);
    public static SubtleCrypto prototype;
    public SubtleCrypto(){}
    native public Object decrypt(Algorithm algorithm, CryptoKey key, ArrayBufferView data);
    native public Object deriveBits(Algorithm algorithm, CryptoKey baseKey, double length);
    native public Object deriveKey(String algorithm, CryptoKey baseKey, Algorithm derivedKeyType, Boolean extractable, String[] keyUsages);
    native public Object deriveKey(Algorithm algorithm, CryptoKey baseKey, Algorithm derivedKeyType, Boolean extractable, String[] keyUsages);
    native public Object deriveKey(Algorithm algorithm, CryptoKey baseKey, String derivedKeyType, Boolean extractable, String[] keyUsages);
    native public Object digest(Algorithm algorithm, ArrayBufferView data);
    native public Object encrypt(Algorithm algorithm, CryptoKey key, ArrayBufferView data);
    native public Object generateKey(Algorithm algorithm, Boolean extractable, String[] keyUsages);
    native public Object importKey(String format, ArrayBufferView keyData, Algorithm algorithm, Boolean extractable, String[] keyUsages);
    native public Object sign(Algorithm algorithm, CryptoKey key, ArrayBufferView data);
    native public Object unwrapKey(String format, ArrayBufferView wrappedKey, CryptoKey unwrappingKey, String unwrapAlgorithm, Algorithm unwrappedKeyAlgorithm, Boolean extractable, String[] keyUsages);
    native public Object unwrapKey(String format, ArrayBufferView wrappedKey, CryptoKey unwrappingKey, Algorithm unwrapAlgorithm, Algorithm unwrappedKeyAlgorithm, Boolean extractable, String[] keyUsages);
    native public Object unwrapKey(String format, ArrayBufferView wrappedKey, CryptoKey unwrappingKey, Algorithm unwrapAlgorithm, String unwrappedKeyAlgorithm, Boolean extractable, String[] keyUsages);
    native public Object verify(Algorithm algorithm, CryptoKey key, ArrayBufferView signature, ArrayBufferView data);
    native public Object wrapKey(String format, CryptoKey key, CryptoKey wrappingKey, Algorithm wrapAlgorithm);
}

