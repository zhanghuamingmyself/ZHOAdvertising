package com.zhanghuaming.myadvertising.jsfuntion.util

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList


class FileHelper{
    companion object {

        @JvmStatic fun deleteDirWihtFile(dir: File?):Int {
            if (dir == null || !dir!!.exists() || !dir!!.isDirectory())
                return 0
            for (file in dir!!.listFiles()) {
                if (file.isFile())
                    file.delete() // 删除所有文件
                else if (file.isDirectory())
                    deleteDirWihtFile(file) // 递规的方式删除文件夹
            }
            dir!!.delete()// 删除目录本身
            return 1
        }

        @JvmStatic fun getLocalList(dir: String): List<String>? {
            val d = File(dir)
            var list: MutableList<String> = ArrayList()//本地列表
            if (!d.exists() || !d.isDirectory()) {
                d.mkdirs()
            }
            val fList = d.listFiles()
            if (fList != null && fList.size != 0) {
                try {
                    var fPath: String
                    for (i in fList!!.indices) {
                        if (fList[i].isDirectory) {
                            list.addAll(getLocalList(fList[i].path)!!)
                        } else {
                            list.add(fList!![i].path)
                        }
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }
                return list
            } else return null
        }




        /**
         * 把文件拷贝到某一目录下
         * @param srcFile
         * @param destDir
         * @return
         */
        @JvmStatic fun copy(srcFile: String, destFile: String): Boolean {
                var file:File = File(srcFile)
                return file.copyRecursively(
                        File(destFile),
                        true,
                        {
                            _, exception -> throw exception
                        })
        }


        /**
         * 移动文件目录到某一路径下
         * @param srcFile
         * @param destDir
         * @return
         */
        @JvmStatic fun move(src: String, destDir: String): Boolean {
            //复制后删除原目录
            if (copy(src, destDir)) {
                deleteFile(File(src))
                return true
            }
            return false
        }

        /**
         * 删除文件（包括目录）
         * @param delFile
         */
        @JvmStatic fun deleteFile(delFile: File) {
            //如果是目录递归删除
            if (delFile.isDirectory) {
                val files = delFile.listFiles()
                for (file in files!!) {
                    deleteFile(file)
                }
            } else {
                delFile.delete()
            }
            //如果不执行下面这句，目录下所有文件都删除了，但是还剩下子目录空文件夹
            delFile.delete()
        }

    }


}