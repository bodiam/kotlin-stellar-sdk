package org.stellar.sdk;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by andrewrogers on 7/1/15.
 */
public class AssetTest {

  @Test
  public void testAssetTypeNative() {
    AssetTypeNative asset = new AssetTypeNative();
    org.stellar.sdk.xdr.Asset xdr = asset.toXdr();
    Asset parsedAsset = Asset.fromXdr(xdr);
    assertTrue(parsedAsset instanceof AssetTypeNative);
  }

  @Test
  public void testAssetTypeCreditAlphaNum4() {
    String code = "USDA";
    KeyPair issuer = KeyPair.random();
    AssetTypeCreditAlphaNum4 asset = new AssetTypeCreditAlphaNum4(code, issuer);
    org.stellar.sdk.xdr.Asset xdr = asset.toXdr();
    AssetTypeCreditAlphaNum4 parsedAsset = (AssetTypeCreditAlphaNum4) Asset.fromXdr(xdr);
    assertEquals(code, asset.getCode());
    assertEquals(issuer.getAccountId(), parsedAsset.getIssuer().getAccountId());
  }

  @Test
  public void testAssetTypeCreditAlphaNum12() {
    String code = "TESTTEST";
    KeyPair issuer = KeyPair.random();
    AssetTypeCreditAlphaNum12 asset = new AssetTypeCreditAlphaNum12(code, issuer);
    org.stellar.sdk.xdr.Asset xdr = asset.toXdr();
    AssetTypeCreditAlphaNum12 parsedAsset = (AssetTypeCreditAlphaNum12) Asset.fromXdr(xdr);
    assertEquals(code, asset.getCode());
    assertEquals(issuer.getAccountId(), parsedAsset.getIssuer().getAccountId());
  }

  @Test
  public void testHashCode() {
    KeyPair issuer1 = KeyPair.random();
    KeyPair issuer2 = KeyPair.random();

    // Equal
    assertEquals(new AssetTypeNative().hashCode(), new AssetTypeNative().hashCode());
    assertEquals(new AssetTypeCreditAlphaNum4("USD", issuer1).hashCode(), new AssetTypeCreditAlphaNum4("USD", issuer1).hashCode());
    assertEquals(new AssetTypeCreditAlphaNum12("ABCDE", issuer1).hashCode(), new AssetTypeCreditAlphaNum12("ABCDE", issuer1).hashCode());

    // Not equal
    assertNotEquals(new AssetTypeNative().hashCode(), new AssetTypeCreditAlphaNum4("USD", issuer1).hashCode());
    assertNotEquals(new AssetTypeNative().hashCode(), new AssetTypeCreditAlphaNum12("ABCDE", issuer1).hashCode());
    assertNotEquals(new AssetTypeCreditAlphaNum4("EUR", issuer1).hashCode(), new AssetTypeCreditAlphaNum4("USD", issuer1).hashCode());
    assertNotEquals(new AssetTypeCreditAlphaNum4("EUR", issuer1).hashCode(), new AssetTypeCreditAlphaNum4("EUR", issuer2).hashCode());
    assertNotEquals(new AssetTypeCreditAlphaNum4("EUR", issuer1).hashCode(), new AssetTypeCreditAlphaNum12("EUROPE", issuer1).hashCode());
    assertNotEquals(new AssetTypeCreditAlphaNum4("EUR", issuer1).hashCode(), new AssetTypeCreditAlphaNum12("EUROPE", issuer2).hashCode());
    assertNotEquals(new AssetTypeCreditAlphaNum12("ABCDE", issuer1).hashCode(), new AssetTypeCreditAlphaNum12("EDCBA", issuer1).hashCode());
    assertNotEquals(new AssetTypeCreditAlphaNum12("ABCDE", issuer1).hashCode(), new AssetTypeCreditAlphaNum12("ABCDE", issuer2).hashCode());
  }

  @Test
  public void testAssetEquals() {
    KeyPair issuer1 = KeyPair.random();
    KeyPair issuer2 = KeyPair.random();

    assertEquals(new AssetTypeNative(), new AssetTypeNative());
    assertEquals(new AssetTypeCreditAlphaNum4("USD", issuer1), new AssetTypeCreditAlphaNum4("USD", issuer1));
    assertEquals(new AssetTypeCreditAlphaNum12("ABCDE", issuer1), new AssetTypeCreditAlphaNum12("ABCDE", issuer1));

    assertNotEquals(new AssetTypeNative(), new AssetTypeCreditAlphaNum4("USD", issuer1));
    assertNotEquals(new AssetTypeNative(), new AssetTypeCreditAlphaNum12("ABCDE", issuer1));
    assertNotEquals(new AssetTypeCreditAlphaNum4("EUR", issuer1), new AssetTypeCreditAlphaNum4("USD", issuer1));
    assertNotEquals(new AssetTypeCreditAlphaNum4("EUR", issuer1), new AssetTypeCreditAlphaNum4("EUR", issuer2));
    assertNotEquals(new AssetTypeCreditAlphaNum12("ABCDE", issuer1), new AssetTypeCreditAlphaNum12("EDCBA", issuer1));
    assertNotEquals(new AssetTypeCreditAlphaNum12("ABCDE", issuer1), new AssetTypeCreditAlphaNum12("ABCDE", issuer2));
  }
}
