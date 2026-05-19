const escapeHtml = (value) =>
  value
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;");

const formatInline = (value) => {
  let output = value;
  output = output.replace(/`([^`]+)`/g, "<code>$1</code>");
  output = output.replace(/\*\*([^*]+)\*\*/g, "<strong>$1</strong>");
  output = output.replace(/\*([^*]+)\*/g, "<em>$1</em>");
  output = output.replace(
    /\[([^\]]+)\]\(([^)]+)\)/g,
    '<a href="$2" target="_blank" rel="noopener noreferrer">$1</a>'
  );
  return output;
};

const renderMarkdown = (markdown) => {
  const lines = markdown.replace(/\r\n/g, "\n").split("\n");
  let html = "";
  let inCode = false;
  let listOpen = false;

  const closeList = () => {
    if (listOpen) {
      html += "</ul>";
      listOpen = false;
    }
  };

  lines.forEach((line) => {
    if (line.trim().startsWith("```")) {
      closeList();
      if (!inCode) {
        inCode = true;
        html += "<pre><code>";
      } else {
        inCode = false;
        html += "</code></pre>";
      }
      return;
    }

    if (inCode) {
      html += `${escapeHtml(line)}\n`;
      return;
    }

    if (line.trim() === "") {
      closeList();
      return;
    }

    const headingMatch = line.match(/^(#{1,3})\s+(.*)/);
    if (headingMatch) {
      closeList();
      const level = headingMatch[1].length;
      const content = formatInline(escapeHtml(headingMatch[2].trim()));
      html += `<h${level}>${content}</h${level}>`;
      return;
    }

    const listMatch = line.match(/^\s*-\s+(.*)/);
    if (listMatch) {
      if (!listOpen) {
        html += "<ul>";
        listOpen = true;
      }
      html += `<li>${formatInline(escapeHtml(listMatch[1].trim()))}</li>`;
      return;
    }

    closeList();
    html += `<p>${formatInline(escapeHtml(line.trim()))}</p>`;
  });

  closeList();
  if (inCode) {
    html += "</code></pre>";
  }

  return html;
};

const setContent = (target, markdown) => {
  target.innerHTML = renderMarkdown(markdown);
};

const loadMarkdownFile = async (path, targetId) => {
  const target = document.getElementById(targetId);
  if (!target) {
    return;
  }

  target.textContent = "加载中...";
  try {
    const response = await fetch(path);
    if (!response.ok) {
      throw new Error("无法读取文件");
    }
    const text = await response.text();
    setContent(target, text);
  } catch (error) {
    target.innerHTML = `<p class="error">读取失败：${escapeHtml(
      String(error.message || error)
    )}</p>`;
  }
};

const handleFileInput = (event) => {
  const file = event.target.files[0];
  if (!file) {
    return;
  }

  const targetId = event.target.dataset.target;
  const target = document.getElementById(targetId);
  if (!target) {
    return;
  }

  const reader = new FileReader();
  reader.onload = () => {
    setContent(target, String(reader.result || ""));
  };
  reader.onerror = () => {
    target.innerHTML = '<p class="error">读取失败，请重试。</p>';
  };
  reader.readAsText(file, "utf-8");
};

document.addEventListener("DOMContentLoaded", () => {
  document.querySelectorAll(".load-sample").forEach((button) => {
    button.addEventListener("click", () => {
      loadMarkdownFile(button.dataset.md, button.dataset.target);
    });
  });

  document.querySelectorAll(".md-input").forEach((input) => {
    input.addEventListener("change", handleFileInput);
  });

  loadMarkdownFile("diary.md", "diary-content");
  loadMarkdownFile("study.md", "study-content");
});
