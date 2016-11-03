package org.strut.amway.core.enumeration;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MonthMessageKeyTest {

    @Test
    public void shouldReturnCorrectMessageKeyForJANUARY() {
        int month = 1;
        MonthMessageKey january = MonthMessageKey.resolve(month);
        assertEquals("janMonth", january.getMessageKey());
    }

    @Test
    public void shouldReturnCorrectMessageKeyForFEBRUARY() {
        int month = 2;
        MonthMessageKey february = MonthMessageKey.resolve(month);
        assertEquals("febMonth", february.getMessageKey());
    }

    @Test
    public void shouldReturnCorrectMessageKeyForMARCH() {
        int month = 3;
        MonthMessageKey march = MonthMessageKey.resolve(month);
        assertEquals("marMonth", march.getMessageKey());
    }

    @Test
    public void shouldReturnCorrectMessageKeyForAPRIL() {
        int month = 4;
        MonthMessageKey april = MonthMessageKey.resolve(month);
        assertEquals("aprMonth", april.getMessageKey());
    }

    @Test
    public void shouldReturnCorrectMessageKeyForMAY() {
        int month = 5;
        MonthMessageKey may = MonthMessageKey.resolve(month);
        assertEquals("mayMonth", may.getMessageKey());
    }

    @Test
    public void shouldReturnCorrectMessageKeyForJUNE() {
        int month = 6;
        MonthMessageKey june = MonthMessageKey.resolve(month);
        assertEquals("junMonth", june.getMessageKey());
    }

    @Test
    public void shouldReturnCorrectMessageKeyForJULY() {
        int month = 7;
        MonthMessageKey july = MonthMessageKey.resolve(month);
        assertEquals("julMonth", july.getMessageKey());
    }

    @Test
    public void shouldReturnCorrectMessageKeyForAUGUST() {
        int month = 8;
        MonthMessageKey august = MonthMessageKey.resolve(month);
        assertEquals("augMonth", august.getMessageKey());
    }

    @Test
    public void shouldReturnCorrectMessageKeyForSEPTEMBER() {
        int month = 9;
        MonthMessageKey september = MonthMessageKey.resolve(month);
        assertEquals("sepMonth", september.getMessageKey());
    }

    @Test
    public void shouldReturnCorrectMessageKeyForOCTOBER() {
        int month = 10;
        MonthMessageKey october = MonthMessageKey.resolve(month);
        assertEquals("octMonth", october.getMessageKey());
    }

    @Test
    public void shouldReturnCorrectMessageKeyForNOVEMBER() {
        int month = 11;
        MonthMessageKey november = MonthMessageKey.resolve(month);
        assertEquals("novMonth", november.getMessageKey());
    }

    @Test
    public void shouldReturnCorrectMessageKeyForDECEMBER() {
        int month = 12;
        MonthMessageKey december = MonthMessageKey.resolve(month);
        assertEquals("decMonth", december.getMessageKey());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionWhenInvalidMonth() {
        int month = 13;
        MonthMessageKey.resolve(month);
    }

}
