const getHeaderSize = h => {
  return Number.parseInt(h.tagName.substring(1));
};

const createNode = h => ({
  h,
  id: h.id,
  text: h.firstChild.textContent,
  sub: [],
});

export const getInnerFrameHeaders = document => {
  if (!document) {
    console.warn('[getInnerFrameHeaders] empty document');
    return [];
  }
  const plainHeaders = [...document.querySelectorAll('h1,h2,h3,h4,h5,h6')];
  console.log('[getInnerFrameHeaders] plainHeaders', plainHeaders);
  const headers = [];
  const stack = [];
  plainHeaders.forEach(h => {
    const size = getHeaderSize(h);
    const node = createNode(h);
    if (!stack.length) {
      headers.push(node);
      stack.push(node);
    } else {
      let recent;
      let added = false;
      while ((recent = stack.pop())) {
        if (getHeaderSize(recent.h) < size) {
          recent.sub.push(node);
          stack.push(recent, node);
          added = true;
          break;
        }
      }
      if (!added) {
        headers.push(node);
        stack.push(node);
      }
    }
  });
  console.log('[getInnerFrameHeaders] headers', headers);
  return headers;
};
