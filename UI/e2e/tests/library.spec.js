const uuidv4 = require('uuid/v4');

const EC = protractor.ExpectedConditions;

async function getLibraryNames() {
  const libraryNameRows = element.all(by.css('div.es-row-highlight'));
  await browser.wait(libraryNameRows.isPresent(), 5000);
  return libraryNameRows.map(el => {
    return el.element(by.tagName('span')).getText();
  });
}

async function getLibraryRowByLibraryName(name) {
  const libraryNameRows = element.all(by.css('div.es-row-highlight'));
  await browser.wait(libraryNameRows.isPresent(), 5000);
  await browser.wait(libraryNameRows.isPresent(), 5000);
  
  return libraryNameRows.filter(async el => {
    const libraryNameText = await el.element(by.tagName('span')).getText();
    return libraryNameText === name;
  }).first();
}

async function getLeaveLibraryButton() {
  const leaveLibraryButton = element.all(by.css('i.es-icon-button-large'));
  await browser.wait(leaveLibraryButton.isPresent(), 5000);
  return leaveLibraryButton.filter(async el => {
    const buttonIconName = await el.getText();
    return buttonIconName === 'navigate_before';
  }).first();
}

describe('Library functionality', function() {
  it('App loads', function() {
    browser.waitForAngularEnabled(false);
    browser.get('http://localhost:4343');

    expect(browser.getTitle()).toEqual('E-Book Server');
  });

  it('Creates a library', async function() {
    browser.waitForAngularEnabled(false);
    await browser.get('http://localhost:4343');

    const libraryName = uuidv4();

    const libraryNameElementsBeforeTest = getLibraryNames();
    expect(libraryNameElementsBeforeTest).not.toContain(libraryName);

    // The libraries have to be loaded from the server, so wait for them to be loaded before the add button will be visible.
    const addLibraryButton = element(by.css('.es-icon-button'));
    await browser.wait(addLibraryButton.isPresent(), 5000);
    await addLibraryButton.click();

    // Have to wait for the modal to pop-up before the input will be available.
    const libraryNameInput = element(by.id('createLibraryNameId'));
    await browser.wait(libraryNameInput.isPresent(), 5000);
    await libraryNameInput.sendKeys(libraryName);

    const okButton = element(by.css('button.btn-primary'));
    await browser.wait(okButton.isPresent(), 5000);
    await okButton.click();

    const closeButton = element(by.css('button.close'));
    const closeButtonPresent = await closeButton.isPresent();
    if (closeButtonPresent) {
      await closeButton.click();
    }

    const createLibraryModal = element(by.id('createLibraryControlModal'));
    await browser.wait(EC.invisibilityOf(createLibraryModal), 5000, 'Create library modal did not close');

    const libraryNameElements = await getLibraryNames();
    expect(libraryNameElements).toContain(libraryName);
  });

  it('Opens and closes library', async function() {
    browser.waitForAngularEnabled(false);
    await browser.get('http://localhost:4343');

    const libraryName = uuidv4();

    // The libraries have to be loaded from the server, so wait for them to be loaded before the add button will be visible.
    const addLibraryButton = element(by.css('.es-icon-button'));
    await browser.wait(addLibraryButton.isPresent(), 5000);
    await addLibraryButton.click();

    // Have to wait for the modal to pop-up before the input will be available.
    const libraryNameInput = element(by.id('createLibraryNameId'));
    await browser.wait(libraryNameInput.isPresent(), 5000);
    await libraryNameInput.sendKeys(libraryName);

    await browser.wait(EC.textToBePresentInElementValue(libraryNameInput, libraryName), 5000);

    const okButton = element(by.css('button.btn-primary'));
    await browser.wait(okButton.isPresent(), 5000);
    await okButton.click();

    const closeButton = element(by.css('button.close'));
    const closeButtonPresent = await closeButton.isPresent();
    if (closeButtonPresent) {
      await closeButton.click();
    }

    const createLibraryModal = element(by.id('createLibraryControlModal'));
    await browser.wait(EC.invisibilityOf(createLibraryModal), 5000, 'Create library modal did not close');

    const libraryRow = await getLibraryRowByLibraryName(libraryName);
    await browser.wait(EC.elementToBeClickable(libraryRow), 5000);
    await libraryRow.click();

    const leaveLibraryButton = await getLeaveLibraryButton();
    await leaveLibraryButton.click();

    const libraryNameElementsAfterLeave = await getLibraryNames();
    expect(libraryNameElementsAfterLeave).toContain(libraryName);
  });
});
