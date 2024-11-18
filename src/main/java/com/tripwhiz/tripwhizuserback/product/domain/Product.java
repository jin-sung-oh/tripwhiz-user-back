package com.tripwhiz.tripwhizuserback.product.domain;

import com.tripwhiz.tripwhizuserback.category.domain.Category;
import com.tripwhiz.tripwhizuserback.category.domain.SubCategory;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pno;

    @Column(nullable = false, length = 50)
    private String pname;

    @Lob
    private String pdesc;

    private int price;


    private boolean delFlag;

    // Image 컬렉션을 List로 관리
//    @ElementCollection
//    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
//    private List<Image> images = new ArrayList<>();

    // Image 컬렉션을 @OneToMany 관계로 변경
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Image> images = new ArrayList<>();

    // 상위 카테고리와의 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id") // 외래 키 이름을 지정 (상위 카테고리 ID와 연결)
    private Category category;

    // 하위 카테고리와의 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id") // 외래 키 이름을 지정 (하위 카테고리 ID와 연결)
    private SubCategory subCategory;

    // 테마와의 관계 설정 (ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_category_id")
    private ThemeCategory themeCategory;


    // 삭제 상태 변경 메서드
    public void changeDelFlag(boolean newDelFlag) {
        this.delFlag = newDelFlag;
    }

    public void addImage(String fileName) {
        images.add(new Image(images.size(), fileName));  // ord 필드 설정
    }

    public void clearImages() {
        images.clear();
    }

    // 상위 카테고리 설정 메서드
    public void setCategory(Category category) {
        this.category = category;
    }

    // 하위 카테고리 설정 메서드
    public void setSubCategory(SubCategory subCategory) {
        this.subCategory = subCategory;
    }

    // 테마 카테고리 설정 메서드
    public void setThemeCategory(ThemeCategory themeCategory) {
        this.themeCategory = themeCategory;
    }
}
