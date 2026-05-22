#!/usr/bin/env python3
"""
PR 审查脚本 - 调用 DeepSeek API 分析 PR 变更并发表评论
"""

import os
import sys
import json
import requests
from pathlib import Path

# 从环境变量读取
GITHUB_TOKEN = os.environ.get("GITHUB_TOKEN")
ANTHROPIC_API_KEY = os.environ.get("ANTHROPIC_API_KEY")
PR_NUMBER = os.environ.get("PR_NUMBER")
REPO = os.environ.get("GITHUB_REPOSITORY")

if not all([GITHUB_TOKEN, ANTHROPIC_API_KEY, PR_NUMBER, REPO]):
    print("❌ Missing required environment variables")
    sys.exit(1)

# 获取 PR 的 diff
def get_pr_diff():
    url = f"https://api.github.com/repos/{REPO}/pulls/{PR_NUMBER}"
    headers = {
        "Authorization": f"Bearer {GITHUB_TOKEN}",
        "Accept": "application/vnd.github.v3.diff"
    }
    resp = requests.get(url, headers=headers)
    resp.raise_for_status()
    return resp.text

# 调用 DeepSeek API 进行分析
def review_diff(diff_text):
    prompt = f"""你是一位资深的代码审查专家。请分析以下 Pull Request 的代码变更（diff），然后输出一份简洁的审查报告。

**要求：**
1. 指出潜在的功能性错误、安全隐患、性能问题、代码风格问题。
2. 如果一切正常，请说“未发现明显问题”。
3. 请用中文回复，每条建议换行。

**Diff 内容：**
{diff_text[:10000]}  # 限制长度避免 token 过多
"""
    headers = {
        "Authorization": f"Bearer {ANTHROPIC_API_KEY}",
        "Content-Type": "application/json"
    }
    payload = {
        "model": "deepseek-v4-flash",
        "messages": [{"role": "user", "content": prompt}],
        "temperature": 0.3,
        "max_tokens": 2000
    }
    resp = requests.post(
        "https://api.deepseek.com",
        headers=headers,
        json=payload
    )
    resp.raise_for_status()
    return resp.json()["choices"][0]["message"]["content"]

# 发布评论到 PR
def post_comment(body):
    url = f"https://api.github.com/repos/{REPO}/issues/{PR_NUMBER}/comments"
    headers = {
        "Authorization": f"Bearer {GITHUB_TOKEN}",
        "Accept": "application/vnd.github.v3+json"
    }
    data = {"body": body}
    resp = requests.post(url, headers=headers, json=data)
    resp.raise_for_status()
    print("✅ Comment posted successfully")

if __name__ == "__main__":
    print("📥 Fetching PR diff...")
    diff = get_pr_diff()
    if not diff.strip():
        print("⚠️ No diff found, skipping review.")
        sys.exit(0)
    
    print("🤖 Requesting DeepSeek review...")
    review = review_diff(diff)
    
    print("💬 Posting comment...")
    comment = f"## 🤖 AI 代码审查报告 (DeepSeek)\n\n{review}"
    post_comment(comment)