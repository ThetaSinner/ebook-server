describe('Library functionality', function() {
  it('App loads', function() {
    browser.waitForAngularEnabled(false);
    browser.get('http://localhost:4343');

    expect(browser.getTitle()).toEqual('E-Book Server');
  });
});
