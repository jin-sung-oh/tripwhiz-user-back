package com.tripwhiz.tripwhizuserback.product.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.tripwhiz.tripwhizuserback.common.dto.PageRequestDTO;
import com.tripwhiz.tripwhizuserback.common.dto.PageResponseDTO;
import com.tripwhiz.tripwhizuserback.product.domain.Product;
import com.tripwhiz.tripwhizuserback.product.domain.QProduct;
import com.tripwhiz.tripwhizuserback.product.domain.QProductTheme;
import com.tripwhiz.tripwhizuserback.product.domain.QThemeCategory;
import com.tripwhiz.tripwhizuserback.product.dto.ProductListDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.stream.Collectors;


@Log4j2
public class ProductSearchImpl extends QuerydslRepositorySupport implements ProductSearch {

    public ProductSearchImpl() {
        super(Product.class);
    }


    @Override
    public PageResponseDTO<ProductListDTO> findByFiltering(Long tno, Long cno, Long scno, PageRequestDTO pageRequestDTO) {
        log.info("-------------------list with filters-----------");
        log.info("Filters: tno = {}, cno = {}, scno = {}", tno, cno, scno);

        // 페이징 설정
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("pno").descending()
        );

        // Q 클래스 인스턴스 생성
        QProduct product = QProduct.product;
        QProductTheme productTheme = QProductTheme.productTheme;
        QThemeCategory themeCategory = QThemeCategory.themeCategory;

        // 기본 쿼리 작성
        JPQLQuery<Product> query = from(product)
                .leftJoin(productTheme).on(productTheme.product.eq(product))
                .leftJoin(themeCategory).on(productTheme.themeCategory.eq(themeCategory))
                .leftJoin(product.attachFiles).fetchJoin();

        // 동적 조건 추가
        BooleanBuilder whereClause = new BooleanBuilder();
        if (tno != null) {
            whereClause.and(themeCategory.tno.eq(tno));
        }
        if (cno != null) {
            whereClause.and(product.category.cno.eq(cno));
        }
        if (scno != null) {
            whereClause.and(product.subCategory.scno.eq(scno));
        }

        whereClause.and(product.delFlag.eq(false));

        query.where(whereClause);

        // 페이징 및 결과 조회
        List<Product> productList = getQuerydsl().applyPagination(pageable, query)
                .select(product)
                .fetch();

        // DTO 변환
        List<ProductListDTO> dtoList = productList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        // 총 개수 조회
        long total = query.fetchCount();

        return PageResponseDTO.<ProductListDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(total)
                .build();

    }

    //JH
    @Override
    public PageResponseDTO<ProductListDTO> searchWithKeywordAndFilters(String keyword, Integer minPrice, Integer maxPrice,
                                                                       Long tno, Long cno, Long scno, PageRequestDTO pageRequestDTO) {
        log.info("Search with keyword: {}, minPrice: {}, maxPrice: {}", keyword, minPrice, maxPrice);

        // 페이징 설정
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("pno").descending()
        );

        // Q 클래스 인스턴스
        QProduct product = QProduct.product;
        QProductTheme productTheme = QProductTheme.productTheme;
        QThemeCategory themeCategory = QThemeCategory.themeCategory;

        // 기본 쿼리 작성
        JPQLQuery<Product> query = from(product)
                .leftJoin(product.attachFiles).fetchJoin()
                .leftJoin(productTheme).on(productTheme.product.eq(product))
                .leftJoin(themeCategory).on(productTheme.themeCategory.eq(themeCategory));

        // 동적 조건 추가
        BooleanBuilder whereClause = new BooleanBuilder();

        if (keyword != null && !keyword.isEmpty()) {
            whereClause.and(
                    product.pname.containsIgnoreCase(keyword)
                            .or(product.pdesc.containsIgnoreCase(keyword))
            );
        }
        if (minPrice != null) {
            whereClause.and(product.price.goe(minPrice));
        }
        if (maxPrice != null) {
            whereClause.and(product.price.loe(maxPrice));
        }

        // tno, cno, scno 필터링
        if (tno != null) {
            whereClause.and(themeCategory.tno.eq(tno));
        }
        if (cno != null) {
            whereClause.and(product.category.cno.eq(cno));
        }
        if (scno != null) {
            whereClause.and(product.subCategory.scno.eq(scno));
        }

        whereClause.and(product.delFlag.eq(false));

        query.where(whereClause);

        // 페이징 및 결과 조회
        List<Product> productList = getQuerydsl().applyPagination(pageable, query)
                .select(product)
                .fetch();

        // DTO 변환
        List<ProductListDTO> dtoList = productList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        // 총 개수 조회
        long total = query.fetchCount();

        return PageResponseDTO.<ProductListDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(total)
                .build();

    }


    private ProductListDTO convertToDto(Product product) {
        return ProductListDTO.builder()
                .pno(product.getPno())
                .pname(product.getPname())
                .price(product.getPrice())
                .pdesc(product.getPdesc())
                .cno(product.getCategory() != null ? product.getCategory().getCno() : null)
                .scno(product.getSubCategory() != null ? product.getSubCategory().getScno() : null)
                .attachFiles(product.getAttachFiles())
                .build();
    }


}
