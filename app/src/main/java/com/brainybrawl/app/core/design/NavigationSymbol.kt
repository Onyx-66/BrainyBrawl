package com.brainybrawl.app.core.design

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.brainybrawl.app.R

enum class NavSymbol { HOME,GAMES,IMAGE,STORE,PROFILE,TROPHY,SETTINGS,PUZZLE,OFFLINE,FRIENDS,AVATARS,FRAMES,EDIT,PASSWORD,DELETE,SIGN_OUT,EMAIL,LINK,CHECK,CLOSE,LEVEL,DOWN }
/** Native vectors from the Lucide set featured in the Morphicons gallery. */
@Composable fun NavigationSymbol(symbol:NavSymbol,modifier:Modifier=Modifier){
    val art=when(symbol){NavSymbol.HOME->"home_icon";NavSymbol.GAMES->"games_icon";NavSymbol.STORE->"store_icon";NavSymbol.PROFILE->"profile_icon";else->null}
    if(art!=null){GameArtwork(art,modifier.size(36.dp));return}
    Icon(painterResource(when(symbol){
        NavSymbol.HOME->R.drawable.ic_lucide_house
        NavSymbol.GAMES->R.drawable.ic_lucide_gamepad_2
        NavSymbol.IMAGE->R.drawable.ic_lucide_image
        NavSymbol.STORE->R.drawable.ic_lucide_shopping_bag
        NavSymbol.PROFILE->R.drawable.ic_lucide_user_round
        NavSymbol.TROPHY->R.drawable.ic_lucide_trophy
        NavSymbol.SETTINGS->R.drawable.ic_lucide_settings
        NavSymbol.PUZZLE->R.drawable.ic_lucide_puzzle
        NavSymbol.OFFLINE->R.drawable.ic_lucide_wifi_off
        NavSymbol.FRIENDS->R.drawable.ic_lucide_users_round
        NavSymbol.AVATARS->R.drawable.ic_lucide_contact_round
        NavSymbol.FRAMES->R.drawable.ic_lucide_scan
        NavSymbol.EDIT->R.drawable.ic_lucide_pencil
        NavSymbol.PASSWORD->R.drawable.ic_lucide_key_round
        NavSymbol.DELETE->R.drawable.ic_lucide_trash
        NavSymbol.SIGN_OUT->R.drawable.ic_lucide_log_out
        NavSymbol.EMAIL->R.drawable.ic_lucide_mail
        NavSymbol.LINK->R.drawable.ic_lucide_link
        NavSymbol.CHECK->R.drawable.ic_lucide_check
        NavSymbol.CLOSE->R.drawable.ic_lucide_x
        NavSymbol.LEVEL->R.drawable.ic_lucide_star
        NavSymbol.DOWN->R.drawable.ic_lucide_chevron_down
    }),null,modifier.size(24.dp))
}
