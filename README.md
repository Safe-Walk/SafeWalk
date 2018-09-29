## Orientações Git

### Criar uma branch
- git branch -b nome-branch
- git push
    
### Merge
- Vá para a branch devil
  - git checkout devil
- Faça o pull
  - git pull
- Se não tiver alteração:
  - Faça o merge com a sua branch
    - git merge nome-branch
- Se tiver alguma alteração:
  - Volte para a sua branch
    - git checkout nome-branch
  - Faça o merge com a branch devil
    - git merge devil
  - Volte para a branch devil
  - Faça o merge com a sua branch
    - git merge nome-branch
