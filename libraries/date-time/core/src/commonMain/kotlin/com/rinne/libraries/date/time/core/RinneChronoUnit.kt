package com.rinne.libraries.date.time.core

import com.rinne.libraries.date.time.core.RinneDateTimeUnit.TimeUnit.Hour
import com.rinne.libraries.date.time.core.RinneDateTimeUnit.TimeUnit.Minute
import com.rinne.libraries.date.time.core.RinneDateTimeUnit.TimeUnit.Nanosecond
import com.rinne.libraries.date.time.core.RinneDateTimeUnit.TimeUnit.Second

sealed interface RinneDateTimeUnit {
    val includes: List<RinneDateTimeUnit>

    sealed interface TimeUnit : RinneDateTimeUnit {

        data object Hour : TimeUnit {
            override val includes = listOf(Hour, Minute, Second, Nanosecond)
        }

        data object Minute : TimeUnit {
            override val includes = listOf(Minute, Second, Nanosecond)
        }

        data object Second : TimeUnit {
            override val includes = listOf(Second, Nanosecond)

        }

        data object Nanosecond : TimeUnit {
            override val includes = listOf(Nanosecond)
        }
    }

    sealed interface DateUnit : RinneDateTimeUnit {
        data object Day : DateUnit {
            override val includes = listOf(Day, Hour, Minute, Second, Nanosecond)
        }

        data object Month : DateUnit {
            override val includes = listOf(Month, Day, Hour, Minute, Second, Nanosecond)
        }

        data object Year : DateUnit {
            override val includes = listOf(Month, Day, Hour, Minute, Second, Nanosecond)
        }
    }
}
