package kr.ms214.cheongwonpay20;

/*
 * Created by 정섭 on 2016-09-22
 * Edited by Cheongwon_SW_Club on 2018-07-13
*/

public class ListViewItem {
    private String titleStr ;
    private String descStr ;
    private String itemCode;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {// 상품 고유번호 설정.
        this.itemCode = itemCode;
    }

    public void setTitle(String title) {// 상품명 설정.
        titleStr = title ;
    }
    public void setDesc(String desc) {// 상품가 설정.
        descStr = desc ;
    }

    public String getTitle() {// 상품명 가져오기.
        return this.titleStr ;
    }
    public String getDesc() {// 상품가 가져오기.
        return this.descStr ;
    }
}
