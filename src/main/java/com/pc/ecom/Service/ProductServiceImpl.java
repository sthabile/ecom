package com.pc.ecom.Service;

import com.pc.ecom.Exceptions.APIException;
import com.pc.ecom.Exceptions.ResourceNotFoundException;
import com.pc.ecom.Model.Category;
import com.pc.ecom.Model.Product;
import com.pc.ecom.Payload.ProductDTO;
import com.pc.ecom.Payload.ProductResponse;
import com.pc.ecom.Repository.CategoryRepository;
import com.pc.ecom.Repository.ProductRepository;
import com.pc.ecom.Utils.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${images.dir}")
    private String imagesDirectory;

    @Value("${images.base.url}")
    private String imagesBaseUrl;

    @Override
    public ProductDTO saveProduct(ProductDTO productDTO, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category",categoryId,"CategoryId"));

        boolean isPresent = false;
        for (Product product : category.getProducts()){
            if(product.getProductName().equals(productDTO.getProductName())){
                isPresent = true;
                break;
            }
        }

        if(isPresent){
           throw new APIException("Product: " + productDTO.getProductName() + " already exists");
        }

        Product product = modelMapper.map(productDTO,Product.class);

        product.setCategory(category);

        product.setImage("default.png");

        product.setSpecialPrice(Utils.computeSpecialPrice(product.getPrice(), product.getDiscount()));

        productRepository.save(product);

        //TODO: Manually map the category
        return modelMapper.map(product,ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Product> productsPage = productRepository.findAll(pageable);

        List<Product> products = productsPage.getContent();

        if(products.isEmpty()){
            throw new APIException("No Products Found");
        }

        List<ProductDTO> productDTOS = products
                .stream()
                .map(product -> {
                           ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
                           productDTO.setImage(constructImageUrl(product.getImage()));
                           return productDTO;
                        }
                )
                .toList();
        return getProductPageResponse(productsPage, productDTOS);
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId,Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category",categoryId,"CategoryId"));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Product> productsPage = productRepository.findProductByCategory(category,pageable);

        List<Product> products = productsPage.getContent();

        if(products.isEmpty()){
            throw new APIException("No Products Found for Category "+categoryId);
        }

        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();

        return getProductPageResponse(productsPage, productDTOS);

    }

    //TODO: Fix issue with LikeIgnoreCase query. Getting an empty result
    //  but getting correct result when running querying directly against the db
    @Override
    public ProductResponse searchProductByKeyword(String keyword,Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, sort);

        Page<Product> productsPage = productRepository.findProductByProductNameLikeIgnoreCase('\''+keyword+'\'',pageRequest);
        List<Product> products = productsPage.getContent();
        if(products.isEmpty()){
            throw new APIException("No Products Found with keyword "+keyword);
        }

        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product,ProductDTO.class))
            .peek(productDTO -> System.out.println(productDTO.getProductName()))
            .toList();

        return getProductPageResponse(productsPage, productDTOS);
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        Product currentProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product",productId,"ProductId"));

        Product productToSave = modelMapper.map(productDTO,Product.class);

        double updatedPrice = productToSave.getPrice();
        double updatedDiscount = productToSave.getDiscount();

        currentProduct.setDiscount(updatedDiscount);
        currentProduct.setPrice(updatedPrice);
        currentProduct.setProductName(productToSave.getProductName());
        currentProduct.setProductDescription(productToSave.getProductDescription());
        currentProduct.setQuantity(productToSave.getQuantity());
        currentProduct.setSpecialPrice(Utils.computeSpecialPrice(updatedPrice,updatedDiscount));

        Product savedProduct = productRepository.save(currentProduct);

        return modelMapper.map(savedProduct,ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product currentProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product",productId,"ProductId"));

        productRepository.delete(currentProduct);

        return modelMapper.map(currentProduct,ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product currentProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product",productId,"ProductId"));

        String fileName = fileService.uploadImage(imagesDirectory, image);

        currentProduct.setImage(fileName);
        Product savedProduct = productRepository.save(currentProduct);

        return modelMapper.map(savedProduct,ProductDTO.class);
    }

    private ProductResponse getProductPageResponse(Page<Product> productsPage, List<ProductDTO> productDTOS) {
        ProductResponse productResponse = new ProductResponse();
        return getProductPageResponse(productResponse, productsPage, productDTOS);
    }

    private ProductResponse getProductPageResponse(ProductResponse productResponse, Page<Product> productsPage, List<ProductDTO> productDTOS) {
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setLastPage(productsPage.isLast());
        productResponse.setTotalElements(productsPage.getTotalElements());

        return productResponse;
    }

    private String constructImageUrl(String imageName) {
        return imagesBaseUrl.endsWith("/") ? imagesBaseUrl + imageName : imagesBaseUrl + "/" + imageName;
    }

}
