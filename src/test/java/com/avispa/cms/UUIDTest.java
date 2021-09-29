package com.avispa.cms;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

/**
 * @author Rafał Hiszpański
 */
class UUIDTest {

    @Test
    void basicTest() {
        System.out.println("Random: " + UUID.randomUUID());
        UUID uuid = UUID.fromString("123e4567-e89b-12d3-a456-556642440000");
        System.out.println("From string: " + uuid);
        String encoded = Base64.getUrlEncoder().encodeToString(asBytes(uuid));
        System.out.println("Original Array: " + Arrays.toString(asBytes(uuid)));
        System.out.println("Encoded: " + encoded);
        System.out.println("Decoded: " + asUuid(Base64.getUrlDecoder().decode(encoded)));
        System.out.println("Decoded Array: " + Arrays.toString(Base64.getUrlDecoder().decode(encoded)));

        System.out.println("Decoded: " + asUuid(Base64.getUrlDecoder().decode("vytxeTZskVKR7C7WgdSP3d==")));

        System.out.println(StringUtils.rightPad("Ej5FZ-ibEtOkVlVmQkQAAA", 24, '='));
    }

    public static UUID asUuid(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long firstLong = bb.getLong();
        long secondLong = bb.getLong();
        return new UUID(firstLong, secondLong);
    }

    public static byte[] asBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
}
