package com.hippout.hippoutuhccore;

import com.hippout.hippoutlocalizationlib.exceptions.*;
import com.hippout.hippoutlocalizationlib.util.*;
import org.junit.*;

import static org.junit.Assert.*;

/**
 * Test class for com.hippout.hippoutlocalizationlib.util.ValidationUtil
 *
 * @author Wyatt Kalmer
 */
public class ValidationUtilTest {
    private static final boolean PRINT_ALL = false;
    // For testing
    // This physically hurt to type out
    private static final String[] currentMinecraftCodes = {
            "af_za",
            "ar_za",
            "ast_es",
            "az_az",
            "be_by",
            "bg_bg",
            "br_fr",
            "ca_es",
            "cs_cz",
            "cy_gb",
            "da_dk",
            "de_at",
            "de_de",
            "el_gr",
            "en_au",
            "en_ca",
            "en_gb",
            "en_nz",
            "en_ud",
            "en_7s",
            "en_us",
            "eo_uy",
            "es_ar",
            "es_es",
            "es_mx",
            "es_uy",
            "es_ve",
            "et_ee",
            "eu_es",
            "fa_ir",
            "fi_fi",
            "fil_ph",
            "fo_fo",
            "fr_fr",
            "fr_ca",
            "fy_nl",
            "ga_ie",
            "gd_gb",
            "gl_es",
            "gv_im",
            "haw",
            "he_il",
            "hi_in",
            "hr_hr",
            "hu_hu",
            "hy_am",
            "id_id",
            "is_is",
            "io",
            "it_it",
            "ja_jp",
            "jbo_en",
            "ka_ge",
            "ko_kr",
            "ksh_de",
            "kw_gb",
            "la_va",
            "lb_lu",
            "li_li",
            "lol_us",
            "it_lt",
            "lv_lv",
            "mi_nz",
            "mk_mk",
            "mn_mn",
            "ms_my",
            "mt_mt",
            "nds_de",
            "nl_nl",
            "nn_no",
            "oc_fr",
            "pl_pl",
            "pt_br",
            "pt_pt",
            "qya_aa",
            "ro_ro",
            "ru_ru",
            "sme",
            "sk_sk",
            "sl_sl",
            "so_so",
            "sq_al",
            "sr_sp",
            "sv_se",
            "swg_de",
            "th_th",
            "tl_ph",
            "tlh_aa",
            "tr_tr",
            "tzl_tzl",
            "uk_ua",
            "ca-val_es",
            "vi_vn",
            "zh_cn",
            "zh_tw"
    };

    @Test
    public void verifyLocaleCodes()
    {
        boolean allGood = true;
        StringBuilder failedTests = new StringBuilder();

        for (String str : currentMinecraftCodes) {
            try {
                if (PRINT_ALL)
                    System.out.print(str + "... ");

                ValidationUtil.validateLocale(str);

                if (PRINT_ALL)
                    System.out.println("passed");

            } catch (LocaleFormatException e) {
                if (!PRINT_ALL)
                    System.out.println(str + "... failed");
                allGood = false;
                failedTests.append(str).append(" ");
            }
        }

        assertTrue("Locale Codes failed: " + failedTests, allGood);
    }

    @Test
    public void verifyNull()
    {
        ValidationUtil.validateLocale("en_us");
        ValidationUtil.validateLocale("en_us", "Error Message.");

        try {
            ValidationUtil.validateLocale(null);
            throw new IllegalStateException("Failed single null.");
        } catch (NullPointerException e) {
        }

        try {
            ValidationUtil.validateLocale(null, "");
            throw new IllegalStateException("Failed null locale.");
        } catch (NullPointerException e) {
        }

        try {
            ValidationUtil.validateLocale("en_us", null);
            throw new IllegalStateException("Failed null error message.");
        } catch (NullPointerException e) {
        }

        try {
            ValidationUtil.validateLocale(null, null);
            throw new IllegalStateException("Failed both null.");
        } catch (NullPointerException e) {
        }
    }
}
