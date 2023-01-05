package com.jsh.erp.service.baidubce;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: origindoris
 * @Title: OcrData
 * @Description:
 * @date: 2022/12/26 14:50
 */
@Data
public class OcrData implements Serializable {
    private String log_id;

    private String direction;

    private String words_result_num;

    private List<Words> words_result;

    @Data
    public static class Words{
        private String words;
    }
}
