// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.prettierjs;

import com.intellij.lang.javascript.buildTools.npm.PackageJsonUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class PrettierImportCodeStyleAction extends AnAction {
  public PrettierImportCodeStyleAction() {
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    final DataContext context = e.getDataContext();
    final PsiFile psiFile = CommonDataKeys.PSI_FILE.getData(context);
    Project project = CommonDataKeys.PROJECT.getData(context);
    final boolean enabledAndVisible = project != null &&
                                      psiFile != null
                                      && isConfigOrPackageJsonWithSection(psiFile);
    e.getPresentation().setEnabledAndVisible(enabledAndVisible);
  }

  private static boolean isConfigOrPackageJsonWithSection(@NotNull PsiFile psiFile) {
    VirtualFile virtualFile = psiFile.getVirtualFile();
    return PrettierUtil.isConfigFile(virtualFile) ||
           PackageJsonUtil.isPackageJsonWithTopLevelProperty(virtualFile, PrettierUtil.PACKAGE_NAME);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    final DataContext context = e.getDataContext();
    Project project = e.getProject();
    final PsiFile psiFile = CommonDataKeys.PSI_FILE.getData(context);
    VirtualFile virtualFile = psiFile != null ? psiFile.getVirtualFile() : null;
    if (virtualFile == null || project == null) {
      return;
    }
    PrettierCompatibleCodeStyleInstaller.install(project, virtualFile, true);
  }
}
