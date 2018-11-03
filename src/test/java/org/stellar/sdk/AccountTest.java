package org.stellar.sdk;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AccountTest {
/*
  @Test
  public void testNullArguments() {
    try {
      new Account(null, 10L);
      fail();
    } catch (NullPointerException e) {}

    try {
      new Account(KeyPair.random(), null);
      fail();
    } catch (NullPointerException e) {}
  }
*/

    @Test
    public void testGetIncrementedSequenceNumber() {
        Account account = new Account(KeyPair.random(), 100L);
        Long incremented;
        incremented = account.getIncrementedSequenceNumber();
        assertEquals(Long.valueOf(100L), (Long) account.getSequenceNumber());
        assertEquals(Long.valueOf(101L), incremented);
        incremented = account.getIncrementedSequenceNumber();
        assertEquals(Long.valueOf(100L), (Long) account.getSequenceNumber());
        assertEquals(Long.valueOf(101L), incremented);
    }

    @Test
    public void testIncrementSequenceNumber() {
        Account account = new Account(KeyPair.random(), 100L);
        Account accountWithIncrementedSequence = account.withIncrementedSequenceNumber();
        assertEquals(Long.valueOf(101L), (Long) accountWithIncrementedSequence.getSequenceNumber());
    }

    @Test
    public void testGetters() {
        KeyPair keypair = KeyPair.random();
        Account account = new Account(keypair, 100L);
        assertEquals(account.getKeypair().getAccountId(), keypair.getAccountId());
        assertEquals(Long.valueOf(100L), (Long) account.getSequenceNumber());
    }
}