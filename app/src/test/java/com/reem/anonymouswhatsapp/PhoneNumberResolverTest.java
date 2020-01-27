package com.reem.anonymouswhatsapp;

import com.reem.anonymouswhatsapp.PhoneNumberResolver;

import org.junit.Test;

public class PhoneNumberResolverTest {

    @Test
    public void resolve() {
        System.out.println( PhoneNumberResolver.resolve("0528543671","+972"));
    }
}