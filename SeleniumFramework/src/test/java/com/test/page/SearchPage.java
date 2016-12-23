package com.test.page;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.vincent.core.page.PageObject;
import com.vincent.core.testcase.Status;

public class SearchPage extends PageObject {

	public By bySearchText() {
		return By.xpath("//input[@id='query']");
	}

	public By bySearchButton() {
		return By.xpath("//input[@id='queryButton']");
	}

	public void input() {
		WebDriver driver = this.newDriver(false);
		driver.get("http://search.maven.org/");
		this.setValue("bySearchText", getData("SearchText"));
		this.report(Status.Pass, "input search text");
	}

	public void clickSearch() {
		this.clickObject(bySearchButton());
	}

	public void checkResultTable() {
		By byResultTable = By.xpath("//table[@id='resultTable']");
		waitElement(byResultTable);
		String expectedSize = this.getData("Size");
		List<WebElement> rows = this.getRows(byResultTable);
		if (expectedSize.startsWith(">")) {
			int size = Integer.valueOf(expectedSize.substring(1, expectedSize.length()));
			this.report(rows.size() > size, "check result table.");
			return;
		}
		if (expectedSize.startsWith("<")) {
			int size = Integer.valueOf(expectedSize.substring(1, expectedSize.length()));
			this.report(rows.size() < size, "check result table.");
			return;
		}

		int size = Integer.valueOf(expectedSize);
		this.report(rows.size() == size, "check result table.");
		this.report(Status.Pass, "test");
	}

}
