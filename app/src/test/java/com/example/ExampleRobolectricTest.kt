package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.ChallengeCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("One Minute", appName)
  }

  @Test
  fun `verify challenge categories are available`() {
    val categories = ChallengeCategory.entries
    assertEquals(10, categories.size)
    assertNotNull(ChallengeCategory.fromString("CREATIVITY"))
    assertEquals(ChallengeCategory.CREATIVITY, ChallengeCategory.fromString("creativity"))
  }
}
