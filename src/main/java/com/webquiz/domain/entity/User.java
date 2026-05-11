package com.webquiz.domain.entity;


import com.webquiz.contact.enums.RoleType;
import com.webquiz.contact.enums.StatusUserType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@Document(collection = "users")
public class User extends BaseEntity {

    @Indexed(unique = true)
    @Field(name = "username")
    private String username;

    @Field(name = "email")
    private String email;

    @Field(name = "password")
    private String password;

    @Field(name = "phone")
    private String phone;

    @Field(name = "first_name")
    private String firstName;

    @Field(name = "last_name")
    private String lastName;

    @Field(name = "role")
    private RoleType role;

    @Field(name = "status")
    private StatusUserType status;
}

