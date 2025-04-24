# 四、 自动化测试 (Automation Testing)

SDET 的核心竞争力所在。

### Q：举例说明，都有哪些功能可以用自动化来进行？

适合自动化的场景通常是 **重复、稳定、客观** 的：

- **回归测试 (Regression Testing):** **(最有价值)** 确保修改不破坏旧功能。
- **冒烟测试 (Smoke Testing):** 构建后快速验证核心流程。
- **API/接口测试:** 接口比 UI 稳定，ROI 高。
- **数据驱动测试:** 同一流程，不同数据输入。
- **跨浏览器/设备测试:** 同一脚本，不同环境执行。
- **构建验证测试 (BVT):** CI 流水线的一部分。
- **繁琐的手动操作:** 批量数据创建/清理。

### Q：如何判断一个功能能否进行自动化测试？

考虑以下因素 (ROI 是关键)：

- **稳定性 (Stability):** 功能是否稳定？频繁变动导致维护成本高。
- **可重复性 (Repeatability):** 每次执行步骤和结果是否一致？(验证码、时间相关功能难)
- **客观性 (Objectivity):** 预期结果是否清晰、程序可判断？(UI美观度、主观体验难)
- **投资回报率 (ROI):** 自动化开发维护成本 vs. 手动执行成本/风险。**频繁执行、耗时长、易出错** 的用例 ROI 高。
- **技术可行性 (Feasibility):** 工具是否支持？元素是否易于定位？(Canvas, Flash, 定制控件难)
- **业务价值 (Business Value):** 是否核心功能？自动化带来的价值多大？

### Q：定位xpath路径都有哪些方法？

XPath 是在 XML/HTML 中查找元素的语言。

- **绝对路径:** `/html/body/div[1]/input` (**脆弱，不推荐**)
- **相对路径:** `//` 开头，更灵活健壮。
- **基本属性定位:**
  ```text 
  //input[@id='username']
  //button[@name='login_button']
  //a[@class='link active']
  //img[@alt='Logo']
  ```

- **文本内容定位:**
  ```text 
  //button[text()='登录']
  //h2[contains(text(), 'Welcome')]
  ```

- **部分属性值匹配 (常用):**
  ```text 
  //input[contains(@id, 'user_')]  // ID 包含 'user_'
  //div[starts-with(@class, 'item-')] // Class 以 'item-' 开头
  //span[ends-with(@data-testid, '-status')] // data-testid 以 '-status' 结尾 (XPath 2.0+)
  ```

- **逻辑运算:**
  ```text 
  //input[@type='text' and @name='query'] // 同时满足
  //div[@role='button' or @role='link'] // 满足任一
  ```

- **轴 (Axes) - 定位相对元素:**
  ```text 
  //label[text()='Password']/following-sibling::input // 查找 'Password' 标签后的输入框
  //td[text()='John Doe']/parent::tr // 查找包含 'John Doe' 的单元格所在的行
  //div[@id='container']/child::button // 查找 ID 为 container 的 div 下的 button 子元素
  //input[@id='firstname']/ancestor::form // 查找 ID 为 firstname 的 input 的 form 祖先元素
  ```

- **索引定位 (谨慎使用):**
  ```text 
  (//input[@type='checkbox'])[2] // 页面上第二个 type 为 checkbox 的 input
  ```


### Q：如何定位一个动态的元素？

ID 或属性部分变化的元素。

1. **找稳定父/祖先:** 定位稳定容器，再相对定位。
2. **用 ****`contains()`****, ****`starts-with()`****, ****`ends-with()`****:** 匹配属性中不变的部分。
   ```text 
   //button[contains(@id, '-submit-button')] // ID 中包含 '-submit-button'
   ```

3. **利用其他稳定属性:** 如 `name`, `placeholder`, `data-*` 自定义属性。
4. **利用文本内容:** `text()` 或 `contains(text(), ...)`。
5. **利用相邻稳定元素 + 轴:** 如 `following-sibling`。
6. **组合条件:** `and`, `or` 结合多个属性。
7. **CSS 选择器:** 有时更简洁，如 `input[id^='user_']`(ID 以 'user \_' 开头)。
8. **显式等待 (Crucial!):** **必须** 使用显式等待 (Explicit Wait) 确保元素加载完成并且可交互，**之后** 再进行定位和操作。

**示例 (Selenium Python - Explicit Wait):**

```python 
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# ... (driver setup)

try:
    # 等待最多10秒，直到 ID 包含 'dyn_button' 的元素变得可点击
    dynamic_button = WebDriverWait(driver, 10).until(
        EC.element_to_be_clickable((By.XPATH, "//button[contains(@id, 'dyn_button')]"))
    )
    dynamic_button.click()
except TimeoutException:
    print("Element not found or not clickable within the time limit.")

```


### Q：如何定位类似于悬浮在web页面上的元素（类似于web上飞来飞去的广告）

位置不固定、可能遮挡其他元素的处理。

1. **检查属性:** 即使位置变，HTML 结构和 ID/Class 可能不变。优先尝试标准定位。
2. **定位父容器:** 悬浮元素通常有特定父 `div`，先定位父容器。
3. **处理 IFrame:** 检查元素是否在 `<iframe>` 内，如果是，需要先 `driver.switchTo().frame(...)`。
4. **显式等待:** 等待元素出现 `visibilityOfElementLocated` 或可点击 `elementToBeClickable`。
5. **处理遮挡 (ElementClickInterceptedException):**
   - **尝试关闭:** 如果有关闭按钮 `X`，先定位并点击它。
   - **JavaScript 点击:** 如果标准 `.click()` 被拦截，用 JS 执行点击。
     ```python 
     # Python Selenium
     element_to_click = driver.find_element(By.ID, 'targetButton')
     driver.execute_script("arguments[0].click();", element_to_click)
     ```

     ```java 
     // Java Selenium
     WebElement elementToClick = driver.findElement(By.id("targetButton"));
     JavascriptExecutor js = (JavascriptExecutor) driver;
     js.executeScript("arguments[0].click();", elementToClick);
     ```

   - **滚动页面:** 尝试将元素滚动到可视区域且不被遮挡的位置。
   - **JavaScript 隐藏遮挡物:** (如果可以) 临时隐藏悬浮元素。
     ```python 
     # Python Selenium - 隐藏 ID 为 'annoyingAd' 的元素
     ad_element = driver.find_element(By.ID, 'annoyingAd')
     driver.execute_script("arguments[0].style.display='none';", ad_element)
     ```


### Q：列举你知道的自动化测试工具

| 类别          | 工具举例                                                                                                          | 主要语言/特点                                                  |
| ----------- | ------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------- |
| **Web UI**​ | Selenium WebDriver                                                                                            | 多语言 (Java, Python, C#, JS), 跨浏览器, 标准                     |
|             | Cypress                                                                                                       | JavaScript/TypeScript, 运行在浏览器内, 调试友好                     |
|             | Playwright                                                                                                    | JS/TS, Python, Java, .NET, 跨浏览器 (Chromium/FF/WebKit), 强大 |
|             | Robot Framework                                                                                               | Python, 关键字驱动, 易上手, 可扩展 (SeleniumLibrary)                |
| **App UI**​ | Appium                                                                                                        | 多语言, 跨平台 (iOS, Android, Win), WebDriver 协议               |
|             | Espresso (Android)                                                                                            | Java/Kotlin, Google 官方, 速度快, 与 Android 集成紧密              |
|             | XCUITest (iOS)                                                                                                | Swift/Objective-C, Apple 官方, 性能好, 稳定                     |
| **API**​    | Postman / Newman                                                                                              | GUI + JS (Newman for CLI/CI), 流行                         |
|             | RestAssured                                                                                                   | Java, DSL 风格, 功能强大                                       |
|             | Requests (Python Lib)                                                                                         | Python, 简洁易用, 广泛用于脚本开发                                   |
|             | HttpRunner                                                                                                    | Python/YAML, 接口测试框架                                      |
|             | k6 (用于 API Load Test)                                                                                         | Go + JavaScript, 现代化的性能测试工具                              |
| **性能**​     | Apache JMeter                                                                                                 | Java (GUI), 开源, 功能强大, 多协议                                |
|             | LoadRunner                                                                                                    | 商业工具, 功能全面                                               |
|             | k6                                                                                                            | (见上) 脚本化性能测试                                             |
| **单元测试**​   | JUnit, TestNG (Java)\\\<br/> pytest, unittest (Python)\\\<br/> NUnit, MSTest (C#)\\\<br/> Jest, Mocha (JS/TS) | (开发者常用，SDET 需掌握)                                         |

***
