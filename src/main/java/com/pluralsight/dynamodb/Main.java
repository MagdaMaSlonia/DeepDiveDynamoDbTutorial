package com.pluralsight.dynamodb;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.pluralsight.dynamodb.dao.CommentDao;
import com.pluralsight.dynamodb.dao.ItemDao;
import com.pluralsight.dynamodb.domain.Comment;
import com.pluralsight.dynamodb.domain.Item;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.EU_WEST_1).build();

        Utils.createTable(client);

        complexQueriesDemo(client);
    }

    private static void complexQueriesDemo(AmazonDynamoDB client) {
        CommentDao commentDao = new CommentDao(client);
        removeAll(commentDao);

        Comment c1 = commentDao.put(newComment("1", "Delivered on time", "10", 5));
        Comment c2 = commentDao.put(newComment("1", "Good stuff!", "11", 4));
        Comment c3 = commentDao.put(newComment("1", "Not as described@", "12", 1));
        Comment c4 = commentDao.put(newComment("2", "So-so...", "10", 3));
        Comment c5 = commentDao.put(newComment("3", "Kitten photos here", "10", 5));
    }

    private static void removeAll(CommentDao commentDao) {

        for(Comment comment : commentDao.getAll()) {
            commentDao.delete(comment.getItemId(), comment.getMessageId());
        }

    }

    private static Comment newComment(String itemId, String msg, String userId, int rating) {
        Comment comment = new Comment();
        comment.setItemId(itemId);
        comment.setMessage(msg);
        comment.setUserId(userId);
        comment.setRating(rating);
        comment.setDateTime(LocalDateTime.now());

        return comment;
    }

    private static void highLevelDemo(AmazonDynamoDB client) {
        ItemDao itemDao = new ItemDao(client);
        Item item1 = itemDao.put(newItem("paczki", "slodkie"));
        Item item2 = itemDao.put(newItem("ciastka", "z karmelem"));
        Item item3 = itemDao.put(newItem("kanapka", "jarska"));

        print(itemDao.getAll());
        pause();

        itemDao.delete(item2.getId());
        print(itemDao.getAll());
    }

    private static void print(List<Item> all) {
        System.out.println(all.stream().map(Item::toString).collect(Collectors.joining("\n")));
    }

    private static Item newItem(String name, String description) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);

        return item;
    }

    private static void pause() {
        System.out.println("PAUSE");

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void lowLevelDemo(AmazonDynamoDB client) {
        ItemDao itemDao = new ItemDao(client);

        Item item = new Item();
        item.setId(UUID.randomUUID().toString());
        item.setName("Bitcoin jest fe");

        itemDao.put(item);
    }
}
