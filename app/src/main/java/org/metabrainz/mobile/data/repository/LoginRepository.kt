package org.metabrainz.mobile.data.repository

import androidx.lifecycle.MutableLiveData
import org.metabrainz.mobile.data.sources.api.entities.AccessToken
import org.metabrainz.mobile.data.sources.api.entities.userdata.UserInfo

interface LoginRepository {

    val accessTokenLiveData: MutableLiveData<AccessToken?>
    val userInfoLiveData: MutableLiveData<UserInfo?>

    fun fetchAccessToken(code: String?)

    fun fetchUserInfo()
}