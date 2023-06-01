package tests;

import com.github.javafaker.Faker;
import config.Config;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import post.CreatePost;
import post.PostResponse;
import utils.Constants;
import lombok.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class TestUser {

    SoftAssert softAssert;

    @BeforeMethod
    public void setUp() {
        softAssert = new SoftAssert();
    }

    @Test
    public void getListTest() {

        Config config = new Config();
        config.setup();

        Response response = given()
                .when().get(Constants.GET_ALL_LISTS);

        System.out.println("Status code iz response je: " + response.getStatusCode());

        System.out.println("JsonPath Evoluaton za polje text je: " + response.jsonPath().get("data[0].text"));
        softAssert.assertEquals(response.jsonPath().get("data[0].text"), "Put any text here 1234 ");
        softAssert.assertAll();
    }

    @Test
    public void getListByUserTest() {

        Config config = new Config();
        config.setup();

        Response response = given()
                .pathParam("id", "60d0fe4f5311236168a109ca")
                .when().get(Constants.GET_LIST_BY_USER);

        softAssert.assertEquals(response.getStatusCode(), 200);
        int a = response.jsonPath().get("data[0].likes");
        softAssert.assertEquals(a, 45);
        softAssert.assertAll();

    }

    @Test
    public void getListByTagTest() {

        Map<String, String> headers = new HashMap<>();
        headers.put("app-id", "64727de75bbbf42d4b35b287");
        Response response = given()
                .baseUri("https://dummyapi.io/data/")
                .basePath("v1/")
                .pathParam("id", "dog")
                .headers(headers)
                .log().all()
                .when().get("tag/dog/post");

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().get("data[0].likes"), "45");

    }

    @Test
    public void getPostByIdTest() {

        Config config = new Config();
        config.setup();
        Response response = given()
                .pathParam("id", "6473c8056bb6233ea4cfdb54")
                .when().get(Constants.GET_POST_BY_ID);

        System.out.println("Status code iz response je: " + response.getStatusCode());
        System.out.println("JsonPath Evoluaton za polje u tag-u je: " + response.jsonPath().get("tags[0]"));
        softAssert.assertEquals(response.getStatusCode(), 200);
        softAssert.assertEquals(response.jsonPath().get("tags[0]"), "animal");
        softAssert.assertAll();
    }

    @Test
    public void createPostViaObjectTest() {
        Config config = new Config();
        config.setup();

        String [] tags2 = {"animal", "canine","dog"};

        CreatePost post = CreatePost.builder()
                .image("https://img.dummyapi.io/photo-1546975554-31053113e977.jpg")
                .likes(45)
                .tags(tags2)
                .text("Put any text here 1234 ")
                .owner("60d0fe4f5311236168a109d9")
                .build();

        PostResponse response = given()
                .body(post)
                .when().post(Constants.CREATE_POST)
                .getBody().as(PostResponse.class);
        String actualUserText = response.getText();
        softAssert.assertEquals(actualUserText, post.getText(), "Actual user Text" + " " +
                "is:" + actualUserText + " " + "but expected Text is:" + post.getText());
        softAssert.assertAll();
    }

    @Test
    public void updatePostTest() {
        Config config = new Config();
        config.setup();
        String [] tags2 = {"animal", "canine","dog"};

        CreatePost post = CreatePost.builder()
                .image("https://img.dummyapi.io/photo-1546975554-31053113e977.jpg")
                .likes(45)
                .tags(tags2)
                .text("Put any text here 1234 ")
                .owner("60d0fe4f5311236168a109d9")
                .build();
        PostResponse response = given()
                .body(post)
                .when().post(Constants.CREATE_POST)
                .getBody().as(PostResponse.class);

        String updatedText = "updatedText";
        post.setText(updatedText);

        String postID = response.getId();
        System.out.println("Post ID koji je vracen iz responsa je:" + " " + postID);

        CreatePost updatedPost = CreatePost.builder()
                .image("https://img.dummyapi.io/photo-1546975554-31053113e977.jpg")
                .likes(45)
                .tags(tags2)
                .text(updatedText)
                .owner("60d0fe4f5311236168a109d9")
                .build();
        PostResponse updatedPostResponse=given()
                .body(updatedPost)
                .pathParam("id",postID)
                .when().put(Constants.UPDATE_POST).getBody().as(PostResponse.class);

        boolean isTextFieldUpdated=updatedPostResponse.getText().equals(updatedText);
        softAssert.assertEquals(isTextFieldUpdated, true);
        softAssert.assertAll();
    }

    @Test
    public void deletePostTest() {
        Config config = new Config();
        config.setup();

        CreatePost post = CreatePost.createPost();
        PostResponse response = given()
                .body(post)
                .when().post(Constants.CREATE_POST)
                .getBody().as(PostResponse.class);
        String postID = response.getId();

        Response response1 = given()
                .pathParam("id", postID)
                .when().delete(Constants.DELETE_POST);

        int actualStatusCode = response1.statusCode();
        softAssert.assertEquals(actualStatusCode, 200, "Ocekivani statusni kod je 200 ali je vratio: " + actualStatusCode);
        String actualPostID = response1.jsonPath().get("id");
        softAssert.assertEquals(actualPostID, postID, "Expected userID is" + postID + "but found " + actualPostID);
        softAssert.assertAll();
    }
}
