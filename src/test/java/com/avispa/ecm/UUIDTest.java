/*
 * Avispa ECM - a small framework for implementing basic ECM solution
 * Copyright (C) 2023 Rafał Hiszpański
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.avispa.ecm;

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
