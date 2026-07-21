@import hexlet.code.dto.UrlsIndexPage
@param UrlsIndexPage page

@template.layout.page(
page = page,
content = @`
    <h1>Список URL</h1>
    <table class="table table-bordered" data-test="urls">
        <thead>
        <tr>
            <th>ID</th>
            <th>Имя</th>
            <th>Последняя проверка</th>
            <th>Код ответа</th>
        </tr>
        </thead>
        <tbody>
        @if(page.getUrls().isEmpty())
            <tr><td colspan="4" class="text-center">Нет добавленных страниц</td></tr>
        @else
            @for(var url : page.getUrls())
                <tr>
                    <td>${url.getId()}</td>
                    <td><a href="/urls/${url.getId()}">${url.getName()}</a></td>
                    @if(page.getLastChecks().containsKey(url.getId()))
                        <td>${page.getLastChecks().get(url.getId()) != null ? page.getLastChecks().get(url.getId()).getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : ""}</td>
                        <td>${page.getLastChecks().get(url.getId()).getStatusCode()}</td>
                    @else
                        <td></td>
                        <td></td>
                    @endif
                </tr>
            @endfor
        @endif
        </tbody>
    </table>
`
)