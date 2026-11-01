// Validates the IBAN in the form through the JSON service and shows the
// result beneath the form, without reloading the page.
document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('ibanForm');
  const input = document.getElementById('ibanRequest');
  const results = document.getElementById('ibanValidationResults');

  form.addEventListener('submit', async (event) => {
    event.preventDefault();
    const url = form.action + '?' + new URLSearchParams({ iban: input.value });
    try {
      const response = await fetch(url, { headers: { Accept: 'application/json' } });
      if (!response.ok) {
        throw new Error('HTTP ' + response.status);
      }
      const result = await response.json();
      results.textContent = 'The IBAN ' + result.displayForm + ' is' + (result.valid ? '' : ' not') + ' valid';
    } catch (error) {
      console.log('Failed! ' + error);
      results.textContent = 'An error occurred whilst validating.';
    }
  });
});
