window.addEventListener('load', function () {
  function corrigirTopbar() {
    var wrapper = document.querySelector('.swagger-ui .topbar .topbar-wrapper');
    if (!wrapper) return false;

    var links = wrapper.querySelectorAll('a');
    if (links.length < 2) return links.length > 0;

    // Pega a imagem do primeiro link antes de remover qualquer coisa
    var primeiroLink = links[0];
    var img = primeiroLink.querySelector('img');

    // Se não tem img no primeiro link, procura em todos
    if (!img) {
      for (var i = 0; i < links.length; i++) {
        img = links[i].querySelector('img');
        if (img) break;
      }
    }

    // Remove todos os links exceto o primeiro
    for (var j = links.length - 1; j > 0; j--) {
      links[j].parentNode.removeChild(links[j]);
    }

    // Garante que a imagem está no primeiro link e visível
    if (img && !primeiroLink.contains(img)) {
      primeiroLink.insertBefore(img, primeiroLink.firstChild);
    }
    if (img) {
      img.style.cssText = 'display:block !important; height:42px; width:auto;';
    }

    // Estilo do primeiro link
    primeiroLink.style.cssText = 'display:flex !important; align-items:center; gap:12px; text-decoration:none; flex-shrink:0;';

    return true;
  }

  var tentativas = 0;
  var intervalo = setInterval(function () {
    if (corrigirTopbar() || tentativas > 30) {
      clearInterval(intervalo);
    }
    tentativas++;
  }, 100);
});
