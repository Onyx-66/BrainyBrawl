package com.brainybrawl.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.test.platform.app.InstrumentationRegistry
import com.brainybrawl.app.feature.store.CurrencyOffers
import com.brainybrawl.app.feature.store.OfferCurrency
import com.brainybrawl.app.ui.theme.BrainyBrawlTheme
import org.junit.*

class StoreOffersTest{
 @get:Rule val rule=createComposeRule()
 @Test fun allEighteenPacksAreBrowsableAndUnavailableCheckoutCannotCharge(){
  rule.setContent{BrainyBrawlTheme{Surface{Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){CurrencyOffers()}}}}
  OfferCurrency.entries.forEach{currency->
   rule.onNodeWithTag("currency-${currency.name}").performScrollTo().performClick()
   (0..5).forEach{index->
    rule.onNodeWithTag("offer-${currency.name}-$index").performScrollTo().assertIsDisplayed()
    rule.onNode(hasClickAction() and hasAnyAncestor(hasTestTag("offer-${currency.name}-$index"))).assertIsNotEnabled()
   }
  }
  rule.onNodeWithTag("currency-GEMS").performScrollTo().performClick()
  rule.waitForIdle()
  val context=InstrumentationRegistry.getInstrumentation().targetContext
  java.io.FileOutputStream(java.io.File(context.getExternalFilesDir(null),"store-offers.png")).use{rule.onRoot().captureToImage().asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG,100,it)}
 }
}
