/**
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.jantd.core.poi.view;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.jantd.core.poi.def.NormalExcelConstants;
import cn.jantd.core.poi.def.TemplateExcelConstants;
import cn.jantd.core.poi.excel.ExcelExportUtil;
import cn.jantd.core.poi.excel.entity.TemplateExportParams;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import org.springframework.stereotype.Controller;

/**
 * Excel 模板导出
 *
 * @author  圈哥
 * @date 2014年6月30日 下午9:15:49
 */
@SuppressWarnings("unchecked")
@Controller(TemplateExcelConstants.JANTD_TEMPLATE_EXCEL_VIEW)
public class JantdTemplateExcelViewBase extends BaseMiniAbstractExcelView {

	public JantdTemplateExcelViewBase() {
		super();
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String codedFileName = "临时文件";
		Workbook workbook = ExcelExportUtil.exportExcel((TemplateExportParams) model.get(TemplateExcelConstants.PARAMS), (Class<?>) model.get(TemplateExcelConstants.CLASS), (List<?>) model.get(TemplateExcelConstants.LIST_DATA), (Map<String, Object>) model.get(TemplateExcelConstants.MAP_DATA));
		if (model.containsKey(NormalExcelConstants.FILE_NAME)) {
			codedFileName = (String) model.get(NormalExcelConstants.FILE_NAME);
		}
		if (workbook instanceof HSSFWorkbook) {
			codedFileName += HSSF;
		} else {
			codedFileName += XSSF;
		}
		if (isIE(request)) {
			codedFileName = java.net.URLEncoder.encode(codedFileName, "UTF8");
		} else {
			codedFileName = new String(codedFileName.getBytes("UTF-8"), "ISO-8859-1");
		}
		response.setHeader("content-disposition", "attachment;filename=" + codedFileName);
		ServletOutputStream out = response.getOutputStream();
		workbook.write(out);
		out.flush();
	}
}
