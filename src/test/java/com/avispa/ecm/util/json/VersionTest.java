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

package com.avispa.ecm.util.json;

import com.avispa.ecm.util.Version;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rafał Hiszpański
 */
class VersionTest {
    private final Version snapshotVersion = new Version("AVISPA ECM", "1.0.3-SNAPSHOT");
    private final Version releaseVersion = new Version("AVISPA ECM", "1.0.3");

    @Test
    void snapshotVersion() {
        assertEquals("AVISPA ECM", snapshotVersion.getApplicationName());
        assertEquals("1.0.3-SNAPSHOT", snapshotVersion.getNumber());
        assertEquals("1.0.3", snapshotVersion.getReleaseNumber());
    }

    @Test
    void releaseVersion() {
        assertEquals("1.0.3", releaseVersion.getNumber());
        assertEquals("1.0.3", releaseVersion.getReleaseNumber());
    }
}