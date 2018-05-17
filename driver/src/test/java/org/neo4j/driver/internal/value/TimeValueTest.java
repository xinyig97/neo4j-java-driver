/*
 * Copyright (c) 2002-2018 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.neo4j.driver.internal.value;

import org.junit.Test;

import java.time.OffsetTime;
import java.time.ZoneOffset;

import org.neo4j.driver.internal.types.InternalTypeSystem;
import org.neo4j.driver.v1.exceptions.value.Uncoercible;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TimeValueTest
{
    @Test
    public void shouldHaveCorrectType()
    {
        OffsetTime time = OffsetTime.now().withOffsetSameInstant( ZoneOffset.ofHoursMinutes( 5, 30 ) );
        TimeValue timeValue = new TimeValue( time );
        assertEquals( InternalTypeSystem.TYPE_SYSTEM.TIME(), timeValue.type() );
    }

    @Test
    public void shouldSupportAsObject()
    {
        OffsetTime time = OffsetTime.of( 19, 0, 10, 1, ZoneOffset.ofHours( -3 ) );
        TimeValue timeValue = new TimeValue( time );
        assertEquals( time, timeValue.asObject() );
    }

    @Test
    public void shouldSupportAsOffsetTime()
    {
        OffsetTime time = OffsetTime.of( 23, 59, 59, 999_999_999, ZoneOffset.ofHoursMinutes( 2, 15 ) );
        TimeValue timeValue = new TimeValue( time );
        assertEquals( time, timeValue.asOffsetTime() );
    }

    @Test
    public void shouldNotSupportAsLong()
    {
        OffsetTime time = OffsetTime.now().withOffsetSameInstant( ZoneOffset.ofHours( -5 ) );
        TimeValue timeValue = new TimeValue( time );

        try
        {
            timeValue.asLong();
            fail( "Exception expected" );
        }
        catch ( Uncoercible ignore )
        {
        }
    }
}
