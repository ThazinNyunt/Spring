package com.phegondev.InventoryMgtSystem.services.impl;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.phegondev.InventoryMgtSystem.dtos.ProductDTO;
import com.phegondev.InventoryMgtSystem.dtos.Response;
import com.phegondev.InventoryMgtSystem.exceptions.NotFoundException;
import com.phegondev.InventoryMgtSystem.models.Category;
import com.phegondev.InventoryMgtSystem.models.Product;
import com.phegondev.InventoryMgtSystem.repositories.CategoryRepository;
import com.phegondev.InventoryMgtSystem.repositories.ProductRepository;
import com.phegondev.InventoryMgtSystem.services.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
	
	private final ProductRepository productRepository;
	private final ModelMapper modelMapper;
	private final CategoryRepository categoryRepository;
	
	private static final String IMAGE_DIRECTORY = System.getProperty("user.dir") + "/proudct-images/";
	
	// After your front-end is setup change the image directory to the front-end you are using
	private static final String IMAGE_DIRECTORY_2= "D:/Java/workplace/project/Spring/ims-react/public/products/";
	
	@Override
	public Response saveProduct(ProductDTO productDTO, MultipartFile imageFile) {
		
		Category category = categoryRepository.findById(productDTO.getCategoryId())
				.orElseThrow(() -> new NotFoundException("Category Not Found"));
		
		// map our dto to product entity
		Product productToSave = Product.builder()
				.name(productDTO.getName())
				.sku(productDTO.getSku())
				.price(productDTO.getPrice())
				.stockQuantiy(productDTO.getStockQuantiy())
				.description(productDTO.getDescription())
				.category(category)
				.build();
		
		if(imageFile != null && !imageFile.isEmpty()) {
			log.info("Image file exist");
//			String imagePath = saveImage(imageFile); // use this when you haven't setup your front-end
			String imagePath = saveImage2(imageFile); // use this whey you are setup your front-end locally but haven't to productions
			productToSave.setImageUrl(imagePath);
		}
		
		// save the product entity
		productRepository.save(productToSave);		
		
		return Response.builder()
				.status(200)
				.message("Product successfully saved")
				.build();
		
	}

	@Override
	public Response updateProduct(ProductDTO productDTO, MultipartFile imageFile) {
		
		// check if product exist
		Product existingProduct = productRepository.findById(productDTO.getProductId())
				.orElseThrow(() -> new NotFoundException("Product Not Found"));
		
		// check if image is associated with the product to update and upload
		if(imageFile != null && !imageFile.isEmpty()) {
			String imagePath = saveImage2(imageFile);
			existingProduct.setImageUrl(imagePath);
		}
		
		// check if category is to be changed for the products
		if(productDTO.getCategoryId() != null && productDTO.getCategoryId() > 0) {
			Category category = categoryRepository.findById(productDTO.getCategoryId())
					.orElseThrow(() -> new NotFoundException("Category Not Found"));
			existingProduct.setCategory(category);
		}
		
		// check if product fields is to be changed and update
		if(productDTO.getName() != null && !productDTO.getName().isBlank()) {
			existingProduct.setName(productDTO.getName());
		}
		
		if(productDTO.getSku() != null && !productDTO.getSku().isBlank()) {
			existingProduct.setSku(productDTO.getSku());
		}
		
		if(productDTO.getDescription() != null && !productDTO.getDescription().isBlank()) {
			existingProduct.setDescription(productDTO.getDescription());
		}
		
		if(productDTO.getPrice() != null && productDTO.getPrice().compareTo(BigDecimal.ZERO) >= 0) {
			existingProduct.setPrice(productDTO.getPrice());
		}
		
		if(productDTO.getStockQuantiy() != null && productDTO.getStockQuantiy() >= 0) {
			existingProduct.setStockQuantiy(productDTO.getStockQuantiy());
		}
		
		// update the product
		productRepository.save(existingProduct);
		
		// Build our response
		return Response.builder()
				.status(200)
				.message("Proudct Updated successfully.")
				.build();
	}

	@Override
	public Response getAllProducts() {
		
		List<Product> productList = productRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
		
		List<ProductDTO> productDTOList = modelMapper.map(productList, new TypeToken<List<ProductDTO>>() {}.getType());
		
		return Response.builder()
				.status(200)
				.message("success")
				.products(productDTOList)
				.build();
	}

	@Override
	public Response getProductById(Long id) {
		
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Product Not Found"));
		
		return Response.builder()
				.status(200)
				.message("success")
				.product(modelMapper.map(product, ProductDTO.class))
				.build();
		
	}

	@Override
	public Response deleteProduct(Long id) {
		productRepository.findById(id)
		.orElseThrow(() -> new NotFoundException("Product Not Found"));
		
		productRepository.deleteById(id);
		
		return Response.builder()
				.status(200)
				.message("Proudct Deleted successfully.")
				.build();
		
		
	}

	@Override
	public Response searchProduct(String input) {
		
		List<Product> products = productRepository.findByNameContainingOrDescriptionContaining(input, input);
		
		if(products.isEmpty()) {
			throw new NotFoundException("Product Not Found");			
		}
		
		List<ProductDTO> prdouctDTOList = modelMapper.map(products, new TypeToken<List<ProductDTO>>() {}.getType());
		
		return Response.builder()
				.status(200)
				.message("success")
				.products(prdouctDTOList)
				.build();
	}
	

	// This saved to the root of your project
	private String saveImage(MultipartFile imageFile) {
		
		// validate image and check if it is greater than 1GB
		if(!imageFile.getContentType().startsWith("image/") || imageFile.getSize() > 1024 * 1024 * 1024) {
			throw new IllegalArgumentException("Only image files under 1GB is allowed.");
		}
		
		// create the directory if it doesn't exist
		File directory = new File(IMAGE_DIRECTORY);
		
		if(!directory.exists()) {
			directory.mkdir();
			log.info("Directory was created");
		}
		
		// generate unique file name for the image
		String uniqueFileName = UUID.randomUUID() + "-" + imageFile.getOriginalFilename();
		
		// Get the absolute path of the image
		String imagePath = IMAGE_DIRECTORY + uniqueFileName;
		
		try {
			File destinationFile = new File(imagePath);
			imageFile.transferTo(destinationFile); // we are writing the image to this folder
		} catch(Exception e) {
			throw new IllegalArgumentException("Error saving Image:" + e.getMessage());
		}
		
		return imagePath;
	}
	
	// This saved image to the public folder in your front-end
	// use this if you have setup your front-end 
	private String saveImage2(MultipartFile imageFile) {
		
		// validate image and check if it is greater than 1GB
		if(!imageFile.getContentType().startsWith("image/") || imageFile.getSize() > 1024 * 1024 * 1024) {
			throw new IllegalArgumentException("Only image files under 1GB is allowed.");
		}
		
		// create the directory if it doesn't exist
		File directory = new File(IMAGE_DIRECTORY_2);
		
		if(!directory.exists()) {
			directory.mkdir();
			log.info("Directory was created");
		}
		
		// generate unique file name for the image
		String uniqueFileName = UUID.randomUUID() + "-" + imageFile.getOriginalFilename();
		
		// Get the absolute path of the image
		String imagePath = IMAGE_DIRECTORY_2 + uniqueFileName;
		
		try {
			File destinationFile = new File(imagePath);
			imageFile.transferTo(destinationFile); // we are writing the image to this folder
		} catch(Exception e) {
			throw new IllegalArgumentException("Error saving Image:" + e.getMessage());
		}
		
		return "products/" + uniqueFileName;
	}

}
