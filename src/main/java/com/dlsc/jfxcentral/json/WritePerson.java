package com.dlsc.jfxcentral.json;

import com.dlsc.jfxcentral.model.Person;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class WritePerson {

    public static void main(String[] args) {
        Person person = new Person("Dirk Lemmermann");
        person.setChampion(true);
        person.setRockstar(true);
        person.setPhoto("dirk.jpg");
        person.setPhoto("dlemmermann.jpg");
        person.setEmail("dlemmermann@gmail.com");
        person.setBlogId("pixel-perfect");
        person.setCompanyId("dlsc");
        person.setLinkedIn("https://www.linkedin.com/in/dlemmermann/");
        person.setTwitter("dlemmermann");
        person.setWebsite("https://dlsc.com");
        person.setId("dlemmermann");

        List<Person> people = new ArrayList<>();
        people.add(person);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(people));
    }
}