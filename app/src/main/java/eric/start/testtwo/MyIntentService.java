package eric.start.testtwo;


import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class MyIntentService extends Service {

    // путь к таблице друзья
    final Uri FRIENDS_URI = Uri
            .parse("content://eric.start.provider.TestTwo/friends");

    // путь к таблице аудио
    final Uri MUSIC_URI = Uri.parse("content://eric.start.provider.TestTwo/audio");

    // поля таблицы "Друзья"
    final String FRIENDS_NAME = "firstName";
    final String FRIENDS_EMAIL = "secondName";
    final String FRIENDS_ID = "_id";

    ExecutorService es;

    // пользователь, для которого создается коллаж фотографий
    String userForCollage;

    // идентификатор друга в списке сообщений
    String idFriendsOfListMessage;

    // задача, которую необходимо выполнить сервису
    String task;

    // идентификатор друга
    String idOfFriend;

    MyRun mr;



    public void onCreate() {
        super.onCreate();
        es = Executors.newFixedThreadPool(1);
    }


    public int onStartCommand(Intent intent, int flags, int startId) {

        task = intent.getStringExtra("Task");

        idOfFriend="";

        switch(task){
            case "countFriendsUser":
                idOfFriend = intent.getStringExtra("id");
            break;

            case "idFriends":
                idOfFriend = intent.getStringExtra("id");
            break;

            case "UrlMessageFriend":
                idFriendsOfListMessage = intent.getStringExtra("id");
            break;

            case "collage":
                //определяем чьи фотографии загружать
                switch(intent.getStringExtra("User")){

                    case "I'am":
                        userForCollage="I'am";
                    break;

                    case "Friend":
                        idOfFriend = intent.getStringExtra("idFriend");
                        userForCollage="Friend";
                    break;

                }

            break;

        }


        mr = new MyRun(task,idOfFriend);
        es.execute(mr);
        return super.onStartCommand(intent, flags, startId);
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

    class MyRun implements Runnable {


        String taskMR;
        String idOfFriendMR;
        Intent intentFriend,intentMessage,intentMain,intentMusic,intentCollage;

        public MyRun(String task,String idOfFriend) {
            this.taskMR = task;
            this.idOfFriendMR = idOfFriend;
        }

        public void run() {

            intentMain = new Intent(MainActivity.BROADCAST_ACTION);
            intentFriend = new Intent(FriendActivity.BROADCAST_ACTION);
            intentMessage = new Intent(MessageActivity.BROADCAST_ACTION);
            intentMusic = new Intent(MusicActivity.BROADCAST_ACTION);
            intentCollage = new Intent(CollageActivity.BROADCAST_ACTION);

            VKRequest request;


            switch(taskMR){

                case "UrlMessageFriend":

                    request = VKApi.users().get(VKParameters.from(VKApiConst.USER_IDS, idFriendsOfListMessage,VKApiConst.FIELDS, "photo_100"));

                    request.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            try {

                                JSONArray JsonArray = response.json.getJSONArray( "response" );
                                String[] urlPhoto = new String[JsonArray.length()];
                                JSONObject JsonObject;

                                for(int i = 0 ; i < JsonArray.length() ; i++ ){

                                    JsonObject = JsonArray.getJSONObject(i);
                                    urlPhoto[i] = JsonObject.getString("photo_100");

                                }

                                intentMessage.putExtra( MessageActivity.PARAM_RESULT , urlPhoto ).putExtra( "Answer" , "UrlMessageFriend" );
                                sendBroadcast(intentMessage);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }


                    });

                    break;

                case "ImageHeaderOfNavigationView":

                    request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "photo_200"));

                    request.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            try {

                                JSONArray JsonArray = response.json.getJSONArray("response");
                                JSONObject JsonObject = JsonArray.getJSONObject(0);

                                String urlPhotoOfHeader = JsonObject.getString("photo_200");
                                intentMain.putExtra(MainActivity.PARAM_RESULT_1, urlPhotoOfHeader).putExtra("Answer","ImageHeaderOfNavigationView");
                                sendBroadcast(intentMain);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    });

                    break;

                case "FriendsTable":

                    request = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "first_name,last_name"));

                    request.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {

                            try {

                                JSONObject JsonObject = response.json.getJSONObject("response");
                                JSONArray JsonArray = JsonObject.getJSONArray("items");

                                Cursor cursorOfTableFriends;
                                JSONObject objectUser;
                                ContentValues dataOfUser;




                                for (int i = 0; i < JsonArray.length(); i++) {

                                    objectUser = JsonArray.getJSONObject(i);
                                    dataOfUser = new ContentValues();

                                    cursorOfTableFriends = getContentResolver().query( Uri.parse("content://eric.start.provider.TestTwo/friends/" + objectUser.getString("id")),null,null,null,null);

                                    if(cursorOfTableFriends.getCount()>0){

                                    }else{
                                        dataOfUser.put(FRIENDS_ID, objectUser.getString("id"));
                                        dataOfUser.put(FRIENDS_NAME, objectUser.getString("first_name")+" "+ objectUser.getString("last_name"));
                                        dataOfUser.put(FRIENDS_EMAIL, objectUser.getInt("online"));
                                        getContentResolver().insert(FRIENDS_URI, dataOfUser);
                                    }

                                    cursorOfTableFriends.close();

                                }

                                String id = "";
                                int k = 0;
                                cursorOfTableFriends = getContentResolver().query( Uri.parse("content://eric.start.provider.TestTwo/friends"),null,null,null,null);


                                if(cursorOfTableFriends.moveToFirst()) {
                                    do {
                                        k = 0;
                                        id = cursorOfTableFriends.getString(cursorOfTableFriends.getColumnIndex("_id"));

                                        for (int i = 0; i < JsonArray.length(); i++) {

                                            objectUser = JsonArray.getJSONObject(i);


                                            if(id.contentEquals(objectUser.getString("id"))){

                                                k++;

                                            }

                                        }

                                        if(k == 0){ getContentResolver().delete(Uri.parse("content://eric.start.provider.TestTwo/friends/" + id),null,null); Log.d("myLogs","++++++++");}

                                    } while (cursorOfTableFriends.moveToNext());
                                }

                                intentMain.putExtra("Answer", "FriendsTable");
                                sendBroadcast(intentMain);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    });
                    break;

                case "UrlPhotoOfFriendsForList":

                    request = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "photo_200_orig"));

                    request.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            try {


                                JSONObject JsonObject = response.json.getJSONObject("response");
                                JSONArray JsonArray = JsonObject.getJSONArray("items");
                                String[] urlPhoto = new String[JsonArray.length()];
                                JSONObject dataOfFriend;

                                for (int i = 0; i < JsonArray.length(); i++) {

                                    dataOfFriend = JsonArray.getJSONObject(i);
                                    urlPhoto[i] = dataOfFriend.getString("photo_200_orig");
                                }

                                intentMain.putExtra(MainActivity.PARAM_RESULT_1, urlPhoto).putExtra("Answer", "UrlPhotoOfFriendsForList");
                                sendBroadcast(intentMain);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }


                    });
                    break;

                case "DataFotFriendActivity":

                    request = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "photo_200_orig","id")); /////а правильно параметры поставлены????

                    request.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {

                            try {


                                JSONObject JsonObject = response.json.getJSONObject("response");
                                JSONArray JsonArray = JsonObject.getJSONArray("items");
                                String[] idOfFriends = new String[JsonArray.length()];
                                String[] urlPhotoHeaderFriends = new String[JsonArray.length()];
                                JSONObject objectFriend;

                                for (int i = 0; i < JsonArray.length(); i++) {

                                    objectFriend = JsonArray.getJSONObject(i);

                                    urlPhotoHeaderFriends[i] = objectFriend.getString("photo_200_orig");

                                    idOfFriends[i] = objectFriend.getString("id");
                                }

                                intentMain.putExtra(MainActivity.PARAM_RESULT_1, urlPhotoHeaderFriends).putExtra(MainActivity.PARAM_RESULT_2, idOfFriends).putExtra("Answer", "DataFotFriendActivity");
                                sendBroadcast(intentMain);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }


                    });

                break;

               case "MusicTable":

                   request=VKApi.audio().get(VKParameters.from(VKApiConst.COUNT, 500)); ///надо передать количество друзей

                   request.executeWithListener(new VKRequest.VKRequestListener() {
                       @Override
                       public void onComplete(VKResponse response) {

                           try{

                               JSONObject JsonObject = response.json.getJSONObject("response");
                               JSONArray JsonArray = JsonObject.getJSONArray("items");
                               JSONObject objectFriend;
                               ContentValues dataOfFriend;
                               Cursor cursorOfTableMusic;

                               for (int i = 0; i < JsonArray.length(); i++) {

                                   objectFriend = JsonArray.getJSONObject(i);
                                   dataOfFriend = new ContentValues();


                                   cursorOfTableMusic = getContentResolver().query(Uri.parse("content://eric.start.provider.TestTwo/audio/" + objectFriend.getString("id")), null, null, null, null);

                                   if(cursorOfTableMusic.getCount()>0){

                                   }else{
                                       dataOfFriend.put("_id", objectFriend.getString("id"));
                                       dataOfFriend.put("artist", objectFriend.getString("artist"));
                                       dataOfFriend.put("title", objectFriend.getString("title"));
                                       dataOfFriend.put("url", objectFriend.getString("url"));
                                       getContentResolver().insert(MUSIC_URI, dataOfFriend);
                                   }

                                   cursorOfTableMusic.close();


                               }

                               intentMusic.putExtra("Answer", "MusicTable");

                               sendBroadcast(intentMusic);

                           }
                           catch(JSONException e) {
                               e.printStackTrace();
                           }



                       }
                   });

                break;

                case "collage":

                    request= new VKRequest("photos.getAll",VKParameters.from("no_service_albums", 1));

                    switch(userForCollage){

                        case "I'am":

                            break;

                        case "Friend":

                            request= new VKRequest("photos.getAll",VKParameters.from(VKApiConst.OWNER_ID, idOfFriendMR,VKApiConst.COUNT,40,"no_service_albums",0));

                            break;

                    }


                    request.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {

                            try {


                                JSONObject JsonObject = response.json.getJSONObject("response");
                                JSONArray JsonArray = JsonObject.getJSONArray("items");

                                JSONObject objectPhoto;

                                String[] photo = new String[JsonArray.length()];

                                for (int i = 0; i < JsonArray.length(); i++) {

                                    objectPhoto = JsonArray.getJSONObject(i);

                                    if (Integer.valueOf(objectPhoto.getString("width")) < 76) {
                                        photo[i] = objectPhoto.getString("photo_75");
                                    }
                                    if (Integer.valueOf(objectPhoto.getString("width")) < 131 && Integer.valueOf(objectPhoto.getString("width")) > 75) {
                                        photo[i] = objectPhoto.getString("photo_130");
                                    }
                                    if (Integer.valueOf(objectPhoto.getString("width")) < 605 && Integer.valueOf(objectPhoto.getString("width")) > 130) {
                                        photo[i] = objectPhoto.getString("photo_604");

                                    } else if (Integer.valueOf(objectPhoto.getString("width")) < 808 && Integer.valueOf(objectPhoto.getString("width")) > 604) {
                                        photo[i] = objectPhoto.getString("photo_807");

                                    } else if (Integer.valueOf(objectPhoto.getString("width")) < 1281 && Integer.valueOf(objectPhoto.getString("width")) > 807) {
                                        photo[i] = objectPhoto.getString("photo_1280");
                                    } else photo[i] = objectPhoto.getString("photo_2560");

                                }


                                intentCollage.putExtra(CollageActivity.PARAM_RESULT, photo).putExtra("length", JsonArray.length());


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            sendBroadcast(intentCollage);

                        }
                    });



                    break;

                case "UrlMusic":

                    request=VKApi.audio().get();

                    request.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {


                            try {

                                JSONObject JsonObject = response.json.getJSONObject("response");
                                JSONArray JsonArray = JsonObject.getJSONArray("items");
                                String[] urlMusic=new String[JsonArray.length()];
                                JSONObject objectMusic;

                                for (int i = 0; i < JsonArray.length(); i++) {

                                    objectMusic = JsonArray.getJSONObject(i);
                                    urlMusic[i] = objectMusic.getString("url");

                                }

                                intentMusic.putExtra(MusicActivity.PARAM_RESULT_1, urlMusic).putExtra("Answer", "UrlMusic");
                                sendBroadcast(intentMusic);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    });

                break;

                case "countFriendsUser":

                    request = VKApi.users().get(VKParameters.from(VKApiConst.USER_IDS, idOfFriendMR,VKApiConst.FIELDS,"status,contacts,city,education,about,quotes,counters"));

                    request.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {



                            try {

                                String mobilePhone="";
                                String homePhone="";
                                String city="";
                                String status="";
                                String universityName="";
                                String about="";
                                String quotes="";

                                JSONArray JsonArray = response.json.getJSONArray("response");
                                JSONObject JsonObject = JsonArray.getJSONObject(0);

                                String firstName = JsonObject.getString("first_name");
                                String lastName = JsonObject.getString("last_name");

                                if(JsonObject.toString().contains("status")){status = JsonObject.getString("status");}//чтоб смайлик отображался
                                if(JsonObject.toString().contains("title")){city = JsonObject.getJSONObject("city").getString("title");}
                                if(JsonObject.toString().contains("mobile_phone")){mobilePhone = JsonObject.getString("mobile_phone");}
                                if(JsonObject.toString().contains("home_phone")){homePhone = JsonObject.getString("home_phone");}
                                if(JsonObject.toString().contains("university_name")){universityName = JsonObject.getString("university_name");}
                                if(JsonObject.toString().contains("about")){about = JsonObject.getString("about");}
                                if(JsonObject.toString().contains("quotes")){quotes = JsonObject.getString("quotes");}



                                intentFriend.putExtra("Answer", "countFriendsUser").putExtra(FriendActivity.firstName, firstName).putExtra(FriendActivity.lastName, lastName)
                                        .putExtra(FriendActivity.status, status).putExtra(FriendActivity.city, city).putExtra(FriendActivity.mobilePhone, mobilePhone).putExtra(FriendActivity.homePhone, homePhone)
                                .putExtra(FriendActivity.universityName, universityName).putExtra(FriendActivity.about, about).putExtra(FriendActivity.quotes, quotes);




                                sendBroadcast(intentFriend);




                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    });


                    break;

                case "Message":

                    request = VKApi.messages().getDialogs(VKParameters.from(VKApiConst.COUNT, 50));

                    request.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {

                            try {

                                JSONObject JsonObject = response.json.getJSONObject("response");
                                JSONArray JsonArray = JsonObject.getJSONArray("items");
                                String[] messageBody = new String[JsonArray.length()];
                                String[] idFriendMessage = new String[JsonArray.length()];

                                int[] outMessage = new int[JsonArray.length()];

                                String idFriends = "";
                                JSONObject objectMessage;
                                JSONObject o;

                                for( int i=0 ; i<JsonArray.length() ; i++ ){

                                    o = JsonArray.getJSONObject(i);
                                    objectMessage = o.getJSONObject("message");
                                    messageBody[i] = objectMessage.getString("body");
                                    outMessage[i] = objectMessage.getInt("out");
                                    idFriendMessage[i] = String.valueOf(objectMessage.getInt("user_id"));
                                    idFriends += idFriendMessage[i]+",";

                                }


                                intentMessage.putExtra("Answer", "Message").putExtra("MessageBody",messageBody).putExtra("user_id",idFriends).putExtra("out",outMessage).putExtra("MessageSendId",idFriendMessage);
                                sendBroadcast(intentMessage);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    });

                break;
                

            }




            }


        }


    }







