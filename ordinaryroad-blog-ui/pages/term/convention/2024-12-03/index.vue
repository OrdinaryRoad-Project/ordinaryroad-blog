<!--
  - MIT License
  -
  - Copyright (c) 2021 苗锦洲
  -
  - Permission is hereby granted, free of charge, to any person obtaining a copy
  - of this software and associated documentation files (the "Software"), to deal
  - in the Software without restriction, including without limitation the rights
  - to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  - copies of the Software, and to permit persons to whom the Software is
  - furnished to do so, subject to the following conditions:
  -
  - The above copyright notice and this permission notice shall be included in all
  - copies or substantial portions of the Software.
  -
  - THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  - IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  - FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  - AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  - LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  - OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  - SOFTWARE.
  -->

<template>
  <div class="py-4">
    <div data-v-ad817de0="" class="v-card__title text-h4 mx-2 mb-0">
      OR博客 平台自律公约
    </div>
    <div style="text-indent: 2em;">
      更新时间：2024年12月03日
    </div>
    <div style="text-indent: 2em;">
      生效时间：2024年12月03日
    </div>
    <br>

    <or-md-vditor
      read-only
      :dark="$vuetify.theme.dark"
      :pre-set-content="commentContent"
    />
  </div>
</template>

<script>
export default {
  layout: 'empty',
  data: () => ({
    commentContent: '### 一、总则\n' +
        '为构建清朗、和谐、绿色、健康的网络环境，维护网络文明秩序，保障用户合法权益，OR博客平台依据并贯彻《中华人民共和国民法典》、《中华人民共和国网络安全法》、《中华人民共和国个人信息保护法》、《中华人民共和国未成年人保护法》、《中华人民共和国预防未成年人犯罪法》、《网络信息内容生态治理规定》等相关法律法规及主管部门的管理政策，与用户共同制定《OR博客平台自律公约》（以下简称“本公约”）。\n' +
        '### 二、平台倡导以下行为\n' +
        '1. 共同参与网络文明建设，制作符合国家大政方针、服务经济社会发展大局、体现时代发展大势、反映强国建设成就的内容作品，为现代化强国建设和中华民族伟大复兴凝心聚力。\n' +
        '2. 共同建立平等友爱的舆论氛围，尊重平台内其他用户。关爱未成年人群体，关照老年人群体，尊重性别平等；不攻击、谩骂、侮辱、诽谤、歧视他人，不侵犯他人合法权益；\n' +
        '3. 鼓励用户发布原创性、优质性内容，重视文字的正确使用，减少用拼音首字母缩写等容易引起歧义的表达，自觉遵守语言文字规范；\n' +
        '4. 提倡用户发布积极健康向上的内容，杜绝未经科学论证的内容传播，倡导用户不造谣、不传谣、不信谣，携手共建清朗网络家园；\n' +
        '5. 用户首次使用前需通过真实身份信息认证，妥善保管账号信息，合理使用账号权限，不以任何方式私自出借或转让用户账号。平台承诺将保障用户个人信息安全，规范处理数据活动；\n' +
        '6. 建议用户提高网络安全防范意识，对网络交友、诱导赌博、贷款、返利、中奖、网络兼职点赞员等网络诈骗高发领域及行为应提高警惕。如发觉异常，可随时向平台举报。\n' +
        '### 三、平台禁止以下行为\n' +
        '用户发布的内容需符合《网络安全法》、《互联网信息服务管理办法》、《网络信息内容生态治理规定》等法律法规，不得发布、传播含有下列内容的信息：\n' +
        '#### （一）危害国家及社会安全等违法信息\n' +
        '1. 反对宪法所确定的基本原则的；\n' +
        '2. 危害国家安全，泄露国家秘密，颠覆国家政权，破坏国家统一的；\n' +
        '3. 损害国家荣誉和利益的；\n' +
        '4. 歪曲、丑化、亵渎、否定英雄烈士事迹和精神，以侮辱、诽谤或者其他方式侵害英雄烈士的姓名、肖像、名誉、荣誉的；\n' +
        '5. 宣扬恐怖主义、极端主义或者煽动实施恐怖活动、极端主义活动的；\n' +
        '6. 煽动民族仇恨、民族歧视，破坏民族团结的；\n' +
        '7. 破坏国家宗教政策，宣扬邪教和封建迷信的；\n' +
        '8. 散布谣言，扰乱经济秩序和社会秩序的；\n' +
        '9. 散布淫秽、色情、赌博、暴力、凶杀、恐怖或者教唆犯罪的；\n' +
        '10. 侮辱或者诽谤他人，侵害他人名誉、隐私和其他合法权益的；\n' +
        '11. 法律、行政法规禁止的其他内容。\n' +
        '#### （二）破坏网络文明生态等不良信息\n' +
        '1. 使用夸张标题，内容与标题严重不符的；\n' +
        '2. 炒作绯闻、丑闻、劣迹等的；\n' +
        '3. 不当评述自然灾害、重大事故等灾难的；\n' +
        '4. 带有性暗示、性挑逗等易使人产生性联想的；\n' +
        '5. 展现血腥、惊悚、残忍等致人身心不适的；\n' +
        '6. 煽动人群歧视、地域歧视等的；\n' +
        '7. 宣扬低俗、庸俗、媚俗内容的；\n' +
        '8. 可能引发未成年人模仿不安全行为和违反社会公德行为、诱导未成年人不良嗜好等的；\n' +
        '9. 其他对网络生态造成不良影响的内容。\n' +
        '#### （三）违背社会公德等失信行为\n' +
        '1. 侵犯他人名誉权、隐私权、专利权、商标权等合法权益；\n' +
        '2. 发布、传送、传播谣言、虚假信息或其他含有不实信息的内容；\n' +
        '3. 发布恶意揣测、曲解平台规范的内容；\n' +
        '4. 虚构、冒用他人身份或以非自身合法所有或持有的公司名称、品牌、商标为名义发布信息；\n' +
        '5. 使用任何非正常手段获取包括但不限于评论、转发、点赞等虚假数据作弊行为。\n' +
        '### 四、未成年人保护\n' +
        '若您是未满18周岁的未成年人，您应在监护人指导下认真阅读本公约，经您的监护人同意后，方可使用平台。若您未取得监护人的同意，监护人可以联系平台处理相关账号，平台有权对相关账号的功能（包括但不限于浏览、发布信息、互动交流等）进行限制。我们将与监护人共同努力，保护未成年人身心健康。如果您是监护人，您亦应履行对未成年人的监护义务，关注未成年人上网安全，引导未成年人合理使用网络，养成良好上网习惯，避免沉迷虚拟的网络空间。\n' +
        '平台将按照《未成年人保护法》《未成年人网络保护条例》等法律法规的规定，积极履行平台义务，保障未成年人的权益，致力于确保平台上未成年人用户的安全与健康，严禁任何人利用平台服务实施虐待、伤害、危害或恶意利用未成年人或者对未成年人灌输错误价值观的行为，严禁用户发布、转发以下影响、危害、诱导未成年人身心健康的内容：\n' +
        '- 带有性暗示、性挑逗等易使人产生性联想的信息内容；\n' +
        '- 展现血腥、惊悚、残忍等致人身心不适的信息内容；\n' +
        '- 宣扬低俗、庸俗、媚俗内容的信息内容；\n' +
        '- 可能引发未成年人模仿不安全行为和违反社会公德行为、诱导未成年人不良嗜好等的信息内容；\n' +
        '- 其他影响、危害、诱导未成年人安全和身心健康的内容。\n' +
        '### 五、平台管理方式\n' +
        'OR博客平台为用户提供了畅通的举报与侵权投诉渠道。用户如果发现违法不良信息或侵犯个人权利内容，可以通过平台进行举报和投诉，平台将在15个工作日内反馈处置进展。具体操作为“联系邮箱”。\n' +
        '对于违反本公约的行为，OR博客平台将采取包括但不限于限制用户账号权限、删除或屏蔽违规内容、封禁违规用户账号等措施，并通知违规用户。如对处置措施有异议，可通过[miaojinzhou@126.com](mailto:miaojinzhou@126.com)向我们申诉。对于屡次违反本公约或造成恶劣影响的，我们将根据情节严重情况，依法向主管部门报告。\n' +
        '### 六、生效和变更\n' +
        '本公约自公布之日起执行，如有任何疑问、意见或建议，可通过[miaojinzhou@126.com](mailto:miaojinzhou@126.com)与我们联系。\n' +
        '在规范实行过程中，我们会结合实际情况，不断完善和更新本公约，并以公告的方式予以公布，请及时关注。'
  })
}
</script>

<style scoped />
