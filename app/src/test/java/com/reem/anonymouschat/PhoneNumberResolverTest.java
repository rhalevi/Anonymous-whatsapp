package com.reem.anonymouschat;

import com.reem.anonymouschat.PhoneNumberResolver;

import org.junit.Test;

public class PhoneNumberResolverTest {

    @Test
    public void resolve() {
        System.out.println( PhoneNumberResolver.resolve("0528543671","+972"));
    }
}