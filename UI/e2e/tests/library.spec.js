const uuidv4 = require('uuid/v4');

async function getLibraryNames() {
  const libraryNameRows = element.all(by.css('div.es-row-highlight'));
  await browser.wait(libraryNameRows.isPresent(), 5000);
  return libraryNameRows.map(el => {
    return el.element(by.tagName('span')).getText();
  });
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

    await element(by.css('button.btn-primary')).click();

    const libraryNameElements = await getLibraryNames();
    expect(libraryNameElements).toContain(libraryName);
  });
});
