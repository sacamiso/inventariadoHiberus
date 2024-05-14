package com.tfg.inventariado.email;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tfg.inventariado.entity.InventarioEntity;
import com.tfg.inventariado.repository.InventarioRepository;

@Component
public class ScheduledTasks {
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private InventarioRepository inventarioRepository;

    public ScheduledTasks(EmailService emailService) {
        this.emailService = emailService;
    }

    //@Scheduled(cron = "0 0 0 1 * ?") // Se ejecuta a las 00:00 del primer día de cada mes
    //@Scheduled(fixedRate = 1) // para hacer las pruebas
    public void sendMonthlyReport() {
    	File attachment;
		try {
			attachment = this.descargarExcelInventario();
			
			String to = "sacamiso@unirioja.es";
	        String subject = "Informe mensual";
	        String text = "Adjunto el informe mensual de la aplicación.";
	        
	        byte[] attachmentData = Files.readAllBytes(attachment.toPath());
            emailService.sendEmail(to, subject, text, attachmentData, attachment.getName());
            
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private File descargarExcelInventario() throws IOException {
		
		Specification<InventarioEntity> spec = Specification.where(null);
		Sort sort = Sort.by("idOficina", "articulo.referencia");
		List<InventarioEntity> listaInventarioEntity = inventarioRepository.findAll(spec, sort);

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet hoja = workbook.createSheet("Inventario");
		
		XSSFCellStyle headerStyle = headerStyle(workbook);
		
		String[] encabezados = {"Identificador oficina", "Referencia artículo", "Stock", "Dirección oficina", "Descripción artículo", "Categoría artículo", "Subcategoría Artículo", "Precio artículo (€)", "IVA artículo (%)", "Fabricante artículo", "Modelo artículo"};
		
		int indiceFila = 0;
		
		XSSFRow fila = hoja.createRow(indiceFila); 
		
		for (int i = 0; i < encabezados.length; i++) {
            String encabezado = encabezados[i];
            XSSFCell celda = fila.createCell(i);
            celda.setCellValue(encabezado);
            celda.setCellStyle(headerStyle);
        }
		
		HashMap<String, XSSFCellStyle> styles = new HashMap<>();
		styles.put("HEADER", headerStyle);
		
		XSSFCellStyle cellStyle = workbook.createCellStyle();
	    cellStyle.setAlignment(HorizontalAlignment.CENTER);
	    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		
		indiceFila++;
        for (InventarioEntity inventario : listaInventarioEntity) {
            fila = hoja.createRow(indiceFila);
            fila.createCell(0).setCellValue(inventario.getIdOficina());
            fila.createCell(1).setCellValue(inventario.getArticulo().getReferencia());
            fila.createCell(2).setCellValue(inventario.getStock());
            String dirOficina = inventario.getOficina().getDireccion() +", " + inventario.getOficina().getCodigoPostal() +", " + inventario.getOficina().getLocalidad() +", " + inventario.getOficina().getPais();
            fila.createCell(3).setCellValue(dirOficina);
            fila.createCell(4).setCellValue(inventario.getArticulo().getDescripcion());
            fila.createCell(5).setCellValue(inventario.getArticulo().getCodCategoria());
            fila.createCell(6).setCellValue(inventario.getArticulo().getCodSubcategoria());
            fila.createCell(7).setCellValue(inventario.getArticulo().getPrecioUnitario());
            fila.createCell(8).setCellValue(inventario.getArticulo().getIva());
            fila.createCell(9).setCellValue(inventario.getArticulo().getFabricante());
            fila.createCell(10).setCellValue(inventario.getArticulo().getModelo());
            indiceFila++;
        }
        
        for (int i = 0; i < encabezados.length; i++) {
            hoja.autoSizeColumn(i);
            hoja.setDefaultColumnStyle(i, cellStyle);
        }
        
        // Crear archivo temporal y escribir el libro de trabajo en él
        File tempFile = File.createTempFile("inventario", ".xlsx");
        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            workbook.write(outputStream);
        } finally {
            workbook.close();
        }
        
        return tempFile;
	}
	
	private XSSFCellStyle headerStyle(XSSFWorkbook workbook) {
		XSSFCellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		XSSFFont headerFont = workbook.createFont();
		headerFont.setColor(IndexedColors.WHITE.getIndex());
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);
		return headerStyle;
	}
}
