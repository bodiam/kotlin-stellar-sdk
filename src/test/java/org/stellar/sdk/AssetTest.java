package org.stellar.sdk;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by andrewrogers on 7/1/15.
 */
public class AssetTest {

  @Test
  public void testAssetTypeNative() {
    NativeAsset asset = NativeAsset.INSTANCE;
    org.stellar.sdk.xdr.Asset xdr = asset.toXdr();
    Asset parsedAsset = Asset.fromXdr(xdr);
    assertTrue(parsedAsset instanceof NativeAsset);
  }

  @Test
  public void testAssetTypeCreditAlphaNum4() {
    String code = "USDA";
    KeyPair issuer = KeyPair.random();
    IssuedAsset4 asset = new IssuedAsset4(code, issuer);
    org.stellar.sdk.xdr.Asset xdr = asset.toXdr();
    IssuedAsset4 parsedAsset = (IssuedAsset4) Asset.fromXdr(xdr);
    assertEquals(code, asset.getCode());
    assertEquals(issuer.getAccountId(), parsedAsset.getIssuer().getAccountId());
  }

  @Test
  public void testAssetTypeCreditAlphaNum12() {
    String code = "TESTTEST";
    KeyPair issuer = KeyPair.random();
    IssuedAsset12 asset = new IssuedAsset12(code, issuer);
    org.stellar.sdk.xdr.Asset xdr = asset.toXdr();
    IssuedAsset12 parsedAsset = (IssuedAsset12) Asset.fromXdr(xdr);
    assertEquals(code, asset.getCode());
    assertEquals(issuer.getAccountId(), parsedAsset.getIssuer().getAccountId());
  }

  @Test
  public void testHashCode() {
    KeyPair issuer1 = KeyPair.random();
    KeyPair issuer2 = KeyPair.random();

    // Equal
    assertEquals(NativeAsset.INSTANCE.hashCode(), NativeAsset.INSTANCE.hashCode());
    assertEquals(new IssuedAsset4("USD", issuer1).hashCode(), new IssuedAsset4("USD", issuer1).hashCode());
    assertEquals(new IssuedAsset12("ABCDE", issuer1).hashCode(), new IssuedAsset12("ABCDE", issuer1).hashCode());

    // Not equal
    assertNotEquals(NativeAsset.INSTANCE.hashCode(), new IssuedAsset4("USD", issuer1).hashCode());
    assertNotEquals(NativeAsset.INSTANCE.hashCode(), new IssuedAsset12("ABCDE", issuer1).hashCode());
    assertNotEquals(new IssuedAsset4("EUR", issuer1).hashCode(), new IssuedAsset4("USD", issuer1).hashCode());
    assertNotEquals(new IssuedAsset4("EUR", issuer1).hashCode(), new IssuedAsset4("EUR", issuer2).hashCode());
    assertNotEquals(new IssuedAsset4("EUR", issuer1).hashCode(), new IssuedAsset12("EUROPE", issuer1).hashCode());
    assertNotEquals(new IssuedAsset4("EUR", issuer1).hashCode(), new IssuedAsset12("EUROPE", issuer2).hashCode());
    assertNotEquals(new IssuedAsset12("ABCDE", issuer1).hashCode(), new IssuedAsset12("EDCBA", issuer1).hashCode());
    assertNotEquals(new IssuedAsset12("ABCDE", issuer1).hashCode(), new IssuedAsset12("ABCDE", issuer2).hashCode());
  }

  @Test
  public void testAssetEquals() {
    KeyPair issuer1 = KeyPair.random();
    KeyPair issuer2 = KeyPair.random();

    assertEquals(NativeAsset.INSTANCE, NativeAsset.INSTANCE);
    assertEquals(new IssuedAsset4("USD", issuer1), new IssuedAsset4("USD", issuer1));
    assertEquals(new IssuedAsset12("ABCDE", issuer1), new IssuedAsset12("ABCDE", issuer1));

    assertNotEquals(NativeAsset.INSTANCE, new IssuedAsset4("USD", issuer1));
    assertNotEquals(NativeAsset.INSTANCE, new IssuedAsset12("ABCDE", issuer1));
    assertNotEquals(new IssuedAsset4("EUR", issuer1), new IssuedAsset4("USD", issuer1));
    assertNotEquals(new IssuedAsset4("EUR", issuer1), new IssuedAsset4("EUR", issuer2));
    assertNotEquals(new IssuedAsset12("ABCDE", issuer1), new IssuedAsset12("EDCBA", issuer1));
    assertNotEquals(new IssuedAsset12("ABCDE", issuer1), new IssuedAsset12("ABCDE", issuer2));
  }
}
