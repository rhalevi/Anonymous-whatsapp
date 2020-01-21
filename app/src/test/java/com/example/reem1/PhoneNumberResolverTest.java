package com.example.reem1;

import org.junit.Test;

import static org.junit.Assert.*;

public class PhoneNumberResolverTest {

    @Test
    public void resolve() {
        System.out.println( PhoneNumberResolver.resolve("0528543671","+972"));
    }
}