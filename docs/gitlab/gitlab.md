✅ 1. SSH 키를 계정별로 따로 만든다
```
ssh-keygen -t ed25519 -f ~/.ssh/id_ed25519_github_personal -C "personal@example.com"
ssh-keygen -t ed25519 -f ~/.ssh/id_ed25519_github_work -C "work@example.com"
ssh-keygen -t ed25519 -f ~/.ssh/id_ed25519_gitlab -C "gitlab@example.com"
```
🔑 그러면 아래 파일이 생긴다:
```
~/.ssh/id_ed25519_github_personal
~/.ssh/id_ed25519_github_personal.pub
~/.ssh/id_ed25519_github_work
~/.ssh/id_ed25519_github_work.pub
~/.ssh/id_ed25519_gitlab
~/.ssh/id_ed25519_gitlab.pub
```
✅ 2. ~/.ssh/config 설정
```
# GitHub 개인용
Host github-personal
  HostName github.com
  User git
  IdentityFile ~/.ssh/id_ed25519_github_personal

# GitHub 회사용
Host github-work
  HostName github.com
  User git
  IdentityFile ~/.ssh/id_ed25519_github_work

# GitLab
Host gitlab
  HostName gitlab.com
  User git
  IdentityFile ~/.ssh/id_ed25519_gitlab
```
🚨 주의: Host는 별칭(alias)이고, HostName은 실제 주소야.

✅ 3. 리포지토리에 맞게 origin 주소 설정
```
git clone git@github-work:your-company/repo.git
git remote set-url origin git@github-work:your-company/repo.git
git remote add gitlab gitlab-bgpark:bgpark82/dkb.git      
```
✅ 4. push
```
gitlab push gitlab master
```
