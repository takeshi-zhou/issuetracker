1.登录。
2.bug
   2.1在bug栏中添加项目。（仅支持maven项目）
   2.2等待Scan完成后，点击view Detail。
   2.3在issue 列表中点击筛选框，查看相应的筛选结果。
   2.4点击指定的issue的detail进行查看。
   2.5在issue detail中，点击issue，并在右侧的version中选择其中一个版本查看详细的错误代码信息。
     2.5.1
         当issue的tag类型非solved时，点击version框中的commit时，会进行当前commit与上一个扫的commit之间的diff。
     2.5.2
         当issue的tag类型为solved时，点击version框中的commit时，会进行当前commit与下一个扫的commit之间的diff。
   2.6.进入diff页面，第一行为选中的issue所在文件，点击之后即可进行这个文件的两个commit之间的diff。

3.clone
   3.1在clone栏中添加项目。（支持java项目）
   3.2等待Scan完成后，点击view Detail。
   3.3在issue 列表中点击筛选框，查看相应的筛选结果。
   3.4点击指定的issue的detail进行查看。
   3.5在issue detail中，点击issue，并在右侧的version中选择任意两个版本比对克隆信息。

4.主界面的dashboard的月周日的的切换，并查看相应的折线图。（bug与clone都需查看）

5.event信息查看。（待补充）

6.项目查询筛选框。
