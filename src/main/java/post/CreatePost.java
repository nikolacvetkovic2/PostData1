package post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.javafaker.Faker;
import lombok.*;

import java.util.Locale;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@AllArgsConstructor

@NoArgsConstructor
public class CreatePost {
        public  String image;
        public int likes;
        @JsonProperty("tags")
        public String[] tags;
        public String text;
        public String owner;

        public static CreatePost createPost(){

                String tag2 [] = {"animal","canine","dog"};
                CreatePost post = CreatePost.builder()
                        .image("https://img.dummyapi.io/photo-1546975554-31053113e977.jpg")
                        .likes(45)
                        .tags(tag2)
                        .text("Put any text here 1234 ")
                        .owner("60d0fe4f5311236168a109d9")
                        .build();
                return post;
    }
}
