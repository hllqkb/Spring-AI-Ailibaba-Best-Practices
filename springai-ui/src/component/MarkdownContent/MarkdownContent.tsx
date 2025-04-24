import 'katex/dist/katex.min.css';
import { useState } from 'react';
import ReactMarkdown from 'react-markdown';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import {
  duotoneDark as darkTheme,
  duotoneLight as lightTheme,
} from 'react-syntax-highlighter/dist/esm/styles/prism';
import rehypeKatex from 'rehype-katex';
import remarkMath from 'remark-math';
import { Button, message, Space, Tooltip } from 'antd';
import { CopyOutlined, PlayCircleOutlined } from '@ant-design/icons';
import { copyContent } from '@/utils/keyboard';
import './index.css';

// Define a Recoil state for the theme

interface Props {
  content: string;
}

const MarkdownContent = (p: Props) => {
  const theme =
    localStorage.getItem('vite-ui-theme') === 'dark' ? darkTheme : lightTheme;
  const [htmlPreview, setHtmlPreview] = useState<Record<string, boolean>>({});

  // 复制代码块内容
  const handleCopy = (code: string) => {
    copyContent(code);
    message.success('代码已复制到剪贴板');
  };

  // 运行HTML代码
  const toggleHtmlPreview = (code: string, index: number) => {
    setHtmlPreview(prev => ({
      ...prev,
      [index]: !prev[index]
    }));
  };

  return (
    <ReactMarkdown
      rehypePlugins={[rehypeKatex]}
      remarkPlugins={[remarkMath]}
      components={{
        code(props) {
          const { children, className, node, style, ref, ...rest } = props;
          const match = /language-(\w+)/.exec(className || '');
          const language = match ? match[1] : '';
          const codeString = String(children).replace(/\n$/, '');
          const codeIndex = JSON.stringify(codeString).length; // 使用代码内容长度作为唯一索引
          
          // 是否显示运行按钮（仅对HTML代码显示）
          const showRunButton = language === 'html';
          
          if (match) {
            return (
              <div className="code-block-container">
                <div className="code-block-header">
                  <span className="code-language">{language}</span>
                  <Space>
                    {showRunButton && (
                      <Tooltip title={htmlPreview[codeIndex] ? '关闭预览' : '运行代码'}>
                        <Button 
                          type="text" 
                          size="small" 
                          icon={<PlayCircleOutlined />} 
                          onClick={() => toggleHtmlPreview(codeString, codeIndex)}
                        />
                      </Tooltip>
                    )}
                    <Tooltip title="复制代码">
                      <Button 
                        type="text" 
                        size="small" 
                        icon={<CopyOutlined />} 
                        onClick={() => handleCopy(codeString)}
                      />
                    </Tooltip>
                  </Space>
                </div>
                <SyntaxHighlighter
                  language={language}
                  PreTag="div"
                  style={theme}
                  {...rest}
                >
                  {codeString}
                </SyntaxHighlighter>
                {showRunButton && htmlPreview[codeIndex] && (
                  <div className="html-preview">
                    <div className="preview-header">预览结果</div>
                    <div className="preview-content">
                      <iframe
                        srcDoc={codeString}
                        title="HTML Preview"
                        sandbox="allow-scripts"
                        width="100%"
                        style={{ border: '1px solid #eee', borderRadius: '4px', minHeight: '200px' }}
                      />
                    </div>
                  </div>
                )}
              </div>
            );
          }
          
          return (
            <code className={className} {...props}>
              {children}
            </code>
          );
        },
      }}
    >
      {p.content}
    </ReactMarkdown>
  );
};

export default MarkdownContent;
