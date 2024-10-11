package com.veblr.android.veblrapp.network;

import androidx.core.text.HtmlCompat;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.veblr.android.veblrapp.datasource.ResponseComment;
import com.veblr.android.veblrapp.datasource.ResponseCountry;
import com.veblr.android.veblrapp.datasource.ResponseHomeFeed;
import com.veblr.android.veblrapp.datasource.ResponseLogin;
import com.veblr.android.veblrapp.datasource.ResponseTag;
import com.veblr.android.veblrapp.datasource.ResponseUser;
import com.veblr.android.veblrapp.datasource.ResponseVideoItem;
import com.veblr.android.veblrapp.datasource.ResponseVideoList;
import com.veblr.android.veblrapp.datasource.ResponseVideoUpload;
import com.veblr.android.veblrapp.model.ChannelDetailResponse;
import com.veblr.android.veblrapp.model.Channels;
import com.veblr.android.veblrapp.model.JSONBody;
import com.veblr.android.veblrapp.model.Response;
import com.veblr.android.veblrapp.model.User;
import com.veblr.android.veblrapp.model.VIdeoItem;

import org.json.JSONObject;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {
    //token validation
    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/user/generate_token.php")
    Call<com.veblr.android.veblrapp.model.Response> getResponseValidToken(@Body JsonObject jsonObject);

    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/user/generate_token.php")
    Call<String> getResponseValid(@Body JsonObject jsonObject);

    //videolist call
    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/video/list-video.php")
    Call<ResponseVideoList> getVideoList(@Body JsonObject jsonObject);

    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/video/list-video.php")
    Call<ResponseVideoList> getVideoListForChannels(@Body JsonObject jsonObject);

    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/video/list-video.php")
    Call<ResponseVideoList> getVideoListForHomeByLocation(@Body JsonObject jsonObject);

    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/video/list-video-home-page.php")
    Call<ResponseVideoList> getVideoListForHomeByRecommendation(@Body JsonObject jsonObject);

    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/follow/followed-channel-video-list.php")
    Call<ResponseVideoList> getVideolistByFollowedChannel(@Body JsonObject jsonObject);

    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/video/index-page-section-complete-data.php")
    Call<ResponseHomeFeed> getVideoListForWebHomePage(@Body JsonObject jsonObject);


    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/video/list-video.php")
    Call<ResponseVideoList> getVideoListFromSearchText(@Body JsonObject jsonObject);

    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/video/list-video.php")
    Call<ResponseVideoList> getVideoListFromCategory(@Body JsonObject jsonObject);

    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/video/fetch-video-data.php")
    Call<ResponseVideoItem> getVideoDetails(@Body JsonObject jsonObject);


    //register guestuser
    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/app_user/app-user-add.php")
    Call<Response> registerGuestUser(@Body JsonObject jsonObject);

    //registerVerifiedUser
    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/user/user-add-through-app.php")
    Call<Response> registerVerifiedUser(@Body JsonObject jsonObject);

    //f
    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/channel/channel-data.php")
    Call<ChannelDetailResponse> getUserDetails(@Body JsonObject jsonObject);

    //likeaVideo
    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/like/like-video.php")
    Call<Response> setlikevideo(@Body JsonObject jsonObject);

    //unlikevideo
    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/like/unlike-video.php")
    Call<Response> setUnlikeVideo(@Body JsonObject jsonObject);

    //videolist of likedvideos by user
    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/like/liked-video-list-by-app-id.php")
    Call<ResponseVideoList> getLikedVideosList(@Body JsonObject jsonObject);

    //videolist of likedvideos by user signin
    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/like/liked-video-list-by-channel-id.php")
    Call<ResponseVideoList> getLikedVideosListBYChannel(@Body JsonObject jsonObject);

    ///getFeaturedChannnels
    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/channel/featured-channel-list.php")
    Call<ChannelDetailResponse> getFeaturedChannels(@Body JsonObject jsonObject);

    //get commnet list of a video
    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/comment/comments-on-video.php")
    Call<ResponseComment> getCommentListOfVideo(@Body JsonObject jsonObject);

    //add a commnet
    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/comment/comment-add.php")
    Call<Response> addCommentToVideo(@Body JsonObject jsonObject);

    //follow a channel
    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/follow/follow-channel.php")
    Call<Response> followaChannel(@Body JsonObject jsonObject);

    //follow a channel
    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/follow/unfollow-channel.php")
    Call<Response> unFollowaChannel(@Body JsonObject jsonObject);

    //get a users followinglist
    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/follow/followed-channel-list-by-app-id.php")
    Call<ChannelDetailResponse> getaUsersFollowingList(@Body JsonObject jsonObject);

    //get a users followinglist after signin
    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/follow/followed-channel-list-by-channel-id.php")
    Call<ChannelDetailResponse> getaUsersFollowingListBYChannel(@Body JsonObject jsonObject);


    //get a TagList
    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/video/video-tags-recommendation.php")
    Call<ResponseTag> getTagList(@Body JsonObject jsonObject);


    //add new  video by user
    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/video/video-add.php")
    Call<ResponseVideoUpload> addNewVideoBYUser(@Body JsonObject jsonObject);

    //login and get response
    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/user/user-login.php")
    Call<ResponseLogin> getLoginResponse(@Body JsonObject jsonObject);


    //get userData after login
    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/user/user-data.php")
    Call<ResponseUser> getUserDetailsAfterLogIn(@Body JsonObject jsonObject);

    Call<ResponseVideoList> getLikedVideosList(String userId, String chId);

    //getcatagories
    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/video/category-list.php")
    Call<ResponseUser> getCatagories(@Body JsonObject jsonObject);

    //getcountries
    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/video/country-list.php")
    Call<ResponseCountry> getCountries(@Body JsonObject jsonObject);

    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/video/language-list.php")
    Call<ResponseUser> getlanguages(@Body JsonObject jsonObject);

    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/video/video-views-update.php")
    Call<Response> sendViewsFromAppUser(@Body JsonObject jsonObject);

    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/video/video-views-update-with-authentication.php")
    Call<Response> sendViewsFromRegAppUser(@Body JsonObject jsonObject);


    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/video/video-detail-update.php")
    Call<Response> sendEditMyAccontVideoDetails(@Body JsonObject jsonObject);

    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/video/video-tags-update.php")
    Call<Response> sendEditMyAccountVideoTagDetails(@Body JsonObject jsonObject);

    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/video/video-delete.php")
    Call<Response> deleteMyAccountVideo(@Body JsonObject jsonObject);

    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/channel/channel-data.php")
    Call<ChannelDetailResponse> getChanneldetails(@Body JsonObject jsonObject);

    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/channel/channel-profile-pic-update.php")
    Call<Response> updateUserImageChannel(@Body JsonObject jsonObject);

    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/channel/channel-detail-update.php")
    Call<ResponseVideoUpload> updateChannelDetails(@Body JsonObject jsonObject);

    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/user/user-profile-pic-update.php")
    Call<Response> updateUserImageDetails(@Body JsonObject jsonObject);

    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/user/user-update.php")
    Call<Response> updateUserAccountDetails(@Body JsonObject jsonObject);

    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/user/check-user-email-id-exist.php")
    Call<Response> checkEmailIdExisted(@Body JsonObject jsonObject);


    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/user/check-user-email-id-status.php")
    Call<Response> checkUserEmailIdVerified(@Body JsonObject jsonObject);

    //check Temp User Mobile Number Status
    @Headers("Content-Type:application/json")
    @POST("veblrAppNews/user/temp-user-add-for-verification.php")
    Call<Response> checktMobNoVerifiedDuringMissedCall(@Body JsonObject jsonObject);

    //check Temp User Mobile Number Status
    @Headers("Content-Type:application/json")
    @POST(" veblrAppNews/user/check-temp-user-mobile-number-status.php")
    Call<Response> checkTempUserIdIsVerifiedOrNot(@Body JsonObject jsonObject);

    @Headers("Content-Type:application/json")
    @POST("veblr-app/veblrAppNews/user/user-add-through-app.php")
    Call<Response>  finalSignUpWithAllTheDetail(@Body JsonObject jsonObject);





}